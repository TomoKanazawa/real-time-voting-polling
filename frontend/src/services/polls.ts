import api from './api';
import { Poll, Vote, VoteResult } from '@/types';

export const getPolls = async (): Promise<Poll[]> => {
  const response = await api.get<Poll[]>('/polls');
  return response.data;
};

export const getPoll = async (id: string): Promise<Poll> => {
  const response = await api.get<Poll>(`/polls/${id}`);
  return response.data;
};

// Define a type for poll creation that doesn't require all fields
export interface CreatePollRequest {
  title: string;
  description: string;
  expiresAt: string;
  active: boolean;
  multipleChoiceAllowed: boolean;
  anonymousVotingAllowed: boolean;
  options: { text: string }[];
  userId: string;
}

export const createPoll = async (poll: CreatePollRequest): Promise<Poll> => {
  try {
    console.log('Sending poll creation request:', JSON.stringify(poll, null, 2));
    const response = await api.post<Poll>('/polls', poll);
    console.log('Poll creation response:', response.data);
    return response.data;
  } catch (error: any) {
    console.error('Poll creation error:', error.response?.data || error.message);
    throw error;
  }
};

export const updatePoll = async (id: string, poll: Partial<Poll>): Promise<Poll> => {
  const response = await api.put<Poll>(`/polls/${id}`, poll);
  return response.data;
};

export const deletePoll = async (id: string): Promise<void> => {
  await api.delete(`/polls/${id}`);
};

export const vote = async (pollId: string, optionId: string, anonymous: boolean): Promise<Vote> => {
  try {
    console.log('Sending vote request:', { pollId, optionId, anonymous });
    const response = await api.post<Vote>('/votes', { pollId, optionId, anonymous });
    console.log('Vote response:', response.data);
    return response.data;
  } catch (error: any) {
    console.error('Vote error:', error.response?.data || error.message);
    throw error;
  }
};

export const getPollResults = async (pollId: string): Promise<VoteResult> => {
  try {
    console.log('Fetching poll results for:', pollId);
    const response = await api.get<VoteResult>(`/polls/${pollId}/results`);
    console.log('Poll results response:', response.data);
    return response.data;
  } catch (error: any) {
    console.error('Poll results error:', error.response?.data || error.message);
    throw error;
  }
};

// WebSocket connection for real-time updates
let socket: WebSocket | null = null;

export const connectToVoteUpdates = (pollId: string, callback: (result: VoteResult) => void): () => void => {
  if (typeof window === 'undefined') return () => {};

  try {
    const wsUrl = process.env.NEXT_PUBLIC_WS_URL || 'ws://localhost:8080/api/ws';
    console.log('Connecting to WebSocket:', wsUrl);
    
    socket = new WebSocket(wsUrl);

    socket.onopen = () => {
      console.log('WebSocket connection opened');
      if (socket) {
        socket.send(JSON.stringify({ type: 'SUBSCRIBE', pollId }));
      }
    };

    socket.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
        console.log('WebSocket message received:', data);
        if (data.type === 'VOTE_UPDATE' && data.pollId === pollId) {
          callback(data.result);
        }
      } catch (error) {
        console.error('Error parsing WebSocket message:', error);
      }
    };

    socket.onerror = (error) => {
      console.error('WebSocket error:', error);
    };

    socket.onclose = () => {
      console.log('WebSocket connection closed');
    };

    return () => {
      console.log('Closing WebSocket connection');
      if (socket) {
        socket.close();
        socket = null;
      }
    };
  } catch (error) {
    console.error('Error setting up WebSocket connection:', error);
    return () => {};
  }
}; 