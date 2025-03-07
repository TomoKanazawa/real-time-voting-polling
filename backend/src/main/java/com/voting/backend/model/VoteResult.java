package com.voting.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteResult {

    private UUID pollId;
    private String pollTitle;
    private long totalVotes;
    private List<OptionResult> options;
    private long timestamp;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionResult {
        private UUID optionId;
        private String optionText;
        private int voteCount;
        private double percentage;
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
                    
                    return OptionResult.builder()
                            .optionId(option.getId())
                            .optionText(option.getText())
                            .voteCount(option.getVoteCount())
                            .percentage(percentage)
                            .build();
                })
                .toList();

        return VoteResult.builder()
                .pollId(poll.getId())
                .pollTitle(poll.getTitle())
                .totalVotes(totalVotes)
                .options(optionResults)
                .timestamp(System.currentTimeMillis())
                .build();
    }
} 