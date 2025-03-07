package com.voting.backend.model;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class VoteResult {

    private UUID pollId;
    private String pollTitle;
    private long totalVotes;
    private List<OptionResult> options;
    private long timestamp;

    // Default constructor
    public VoteResult() {
    }

    // All-args constructor
    public VoteResult(UUID pollId, String pollTitle, long totalVotes, List<OptionResult> options, long timestamp) {
        this.pollId = pollId;
        this.pollTitle = pollTitle;
        this.totalVotes = totalVotes;
        this.options = options;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public UUID getPollId() {
        return pollId;
    }

    public void setPollId(UUID pollId) {
        this.pollId = pollId;
    }

    public String getPollTitle() {
        return pollTitle;
    }

    public void setPollTitle(String pollTitle) {
        this.pollTitle = pollTitle;
    }

    public long getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(long totalVotes) {
        this.totalVotes = totalVotes;
    }

    public List<OptionResult> getOptions() {
        return options;
    }

    public void setOptions(List<OptionResult> options) {
        this.options = options;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static class OptionResult {
        private UUID optionId;
        private String optionText;
        private int voteCount;
        private double percentage;

        // Default constructor
        public OptionResult() {
        }

        // All-args constructor
        public OptionResult(UUID optionId, String optionText, int voteCount, double percentage) {
            this.optionId = optionId;
            this.optionText = optionText;
            this.voteCount = voteCount;
            this.percentage = percentage;
        }

        // Getters and setters
        public UUID getOptionId() {
            return optionId;
        }

        public void setOptionId(UUID optionId) {
            this.optionId = optionId;
        }

        public String getOptionText() {
            return optionText;
        }

        public void setOptionText(String optionText) {
            this.optionText = optionText;
        }

        public int getVoteCount() {
            return voteCount;
        }

        public void setVoteCount(int voteCount) {
            this.voteCount = voteCount;
        }

        public double getPercentage() {
            return percentage;
        }

        public void setPercentage(double percentage) {
            this.percentage = percentage;
        }

        // Builder class
        public static class OptionResultBuilder {
            private UUID optionId;
            private String optionText;
            private int voteCount;
            private double percentage;

            public OptionResultBuilder optionId(UUID optionId) {
                this.optionId = optionId;
                return this;
            }

            public OptionResultBuilder optionText(String optionText) {
                this.optionText = optionText;
                return this;
            }

            public OptionResultBuilder voteCount(int voteCount) {
                this.voteCount = voteCount;
                return this;
            }

            public OptionResultBuilder percentage(double percentage) {
                this.percentage = percentage;
                return this;
            }

            public OptionResult build() {
                return new OptionResult(optionId, optionText, voteCount, percentage);
            }
        }

        public static OptionResultBuilder builder() {
            return new OptionResultBuilder();
        }
    }

    public static VoteResult createFrom(Poll poll) {
        long totalVotes = poll.getOptions().stream()
                .mapToInt(PollOption::getVoteCount)
                .sum();

        List<OptionResult> optionResults = poll.getOptions().stream()
                .map(option -> {
                    double percentage = totalVotes > 0 
                            ? (double) option.getVoteCount() / totalVotes * 100 
                            : 0.0;
                    
                    return new OptionResult.OptionResultBuilder()
                            .optionId(option.getId())
                            .optionText(option.getText())
                            .voteCount(option.getVoteCount())
                            .percentage(percentage)
                            .build();
                })
                .collect(Collectors.toList());

        return new VoteResultBuilder()
                .pollId(poll.getId())
                .pollTitle(poll.getTitle())
                .totalVotes(totalVotes)
                .options(optionResults)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    // Builder class
    public static class VoteResultBuilder {
        private UUID pollId;
        private String pollTitle;
        private long totalVotes;
        private List<OptionResult> options;
        private long timestamp;

        public VoteResultBuilder pollId(UUID pollId) {
            this.pollId = pollId;
            return this;
        }

        public VoteResultBuilder pollTitle(String pollTitle) {
            this.pollTitle = pollTitle;
            return this;
        }

        public VoteResultBuilder totalVotes(long totalVotes) {
            this.totalVotes = totalVotes;
            return this;
        }

        public VoteResultBuilder options(List<OptionResult> options) {
            this.options = options;
            return this;
        }

        public VoteResultBuilder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public VoteResult build() {
            return new VoteResult(pollId, pollTitle, totalVotes, options, timestamp);
        }
    }

    public static VoteResultBuilder builder() {
        return new VoteResultBuilder();
    }
} 