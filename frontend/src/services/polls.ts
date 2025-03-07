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
}

export const createPoll = async (poll: CreatePollRequest): Promise<Poll> => {
  const response = await api.post<Poll>('/polls', poll);
  return response.data;
};

export const updatePoll = async (id: string, poll: Partial<Poll>): Promise<Poll> => {
  const response = await api.put<Poll>(`/polls/${id}`, poll);
  return response.data;
};

export const deletePoll = async (id: string): Promise<void> => {
  await api.delete(`/polls/${id}`);
};

export const vote = async (pollId: string, optionId: string, anonymous: boolean): Promise<Vote> => {
  const response = await api.post<Vote>('/votes', { pollId, optionId, anonymous });
  return response.data;
};

export const getPollResults = async (pollId: string): Promise<VoteResult> => {
  const response = await api.get<VoteResult>(`/polls/${pollId}/results`);
  return response.data;
};

// WebSocket connection for real-time updates
let socket: WebSocket | null = null;

export const connectToVoteUpdates = (pollId: string, callback: (result: VoteResult) => void): () => void => {
  if (typeof window === 'undefined') return () => {};

  const wsUrl = process.env.NEXT_PUBLIC_WS_URL || 'ws://localhost:8080/api/ws';
  socket = new WebSocket(wsUrl);

  socket.onopen = () => {
    if (socket) {
      socket.send(JSON.stringify({ type: 'SUBSCRIBE', pollId }));
    }
  };

  socket.onmessage = (event) => {
    try {
      const data = JSON.parse(event.data);
      if (data.type === 'VOTE_UPDATE' && data.pollId === pollId) {
        callback(data.result);
      }
    } catch (error) {
      console.error('Error parsing WebSocket message:', error);
    }
  };

  return () => {
    if (socket) {
      socket.close();
      socket = null;
    }
  };
}; 