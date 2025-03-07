export interface User {
  id: string;
  username: string;
  email: string;
  role: string;
}

export interface Poll {
  id: string;
  title: string;
  description: string;
  createdAt: string;
  expiresAt: string;
  active: boolean;
  options: PollOption[];
  createdBy: User;
  multipleChoiceAllowed: boolean;
  anonymousVotingAllowed: boolean;
}

export interface PollOption {
  id: string;
  text: string;
  voteCount: number;
}

export interface Vote {
  id: string;
  pollId: string;
  userId?: string;
  optionId: string;
  timestamp: string;
  anonymous: boolean;
}

export interface VoteResult {
  pollId: string;
  pollTitle: string;
  totalVotes: number;
  options: OptionResult[];
  timestamp: number;
}

export interface OptionResult {
  optionId: string;
  optionText: string;
  voteCount: number;
  percentage: number;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface ApiError {
  message: string;
  status: number;
  errors?: Record<string, string[]>;
}

export interface ApiErrorResponse {
  response?: {
    data?: {
      message?: string;
    };
  };
  message: string;
} 