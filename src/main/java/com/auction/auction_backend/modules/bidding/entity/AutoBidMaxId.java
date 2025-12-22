package com.auction.auction_backend.modules.bidding.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AutoBidMaxId implements Serializable {
    private Long product;
    private Long bidder;
}
