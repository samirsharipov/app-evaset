package uz.pdp.springsecurity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.Branch;
import uz.pdp.springsecurity.entity.Outlay;
import uz.pdp.springsecurity.entity.Purchase;
import uz.pdp.springsecurity.entity.Trade;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.payload.InfoDto;
import uz.pdp.springsecurity.repository.BranchRepository;
import uz.pdp.springsecurity.repository.OutlayRepository;
import uz.pdp.springsecurity.repository.PurchaseRepository;
import uz.pdp.springsecurity.repository.TradeRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InfoService {
    private final PurchaseRepository purchaseRepository;
    private final TradeRepository tradeRepository;
    @Autowired
    OutlayRepository outlayRepository;
    @Autowired
    BranchRepository branchRepository;

    public ApiResponse getInfoByBusiness(UUID businessId) {
        List<Purchase> purchaseList = purchaseRepository.findAllByBranch_BusinessId(businessId);
//        if (purchaseList.isEmpty()) return new ApiResponse("NOT FOUND", false);
        double allPurchase = 0;
        double allMyDebt = 0;
        double allTrade = 0;
        double allTradeDebt = 0;
        for (Purchase purchase : purchaseList) {
            allPurchase += purchase.getTotalSum();
            allMyDebt += purchase.getDebtSum();
        }

        List<Trade> tradeList = tradeRepository.findAllByBranch_BusinessId(businessId);
//        if (tradeList.isEmpty()) return new ApiResponse("NOT FOUND", false);
        for (Trade trade : tradeList) {
            allTrade += trade.getTotalSum();
            allTradeDebt += trade.getDebtSum();
        }
        InfoDto infoDto = new InfoDto();
        infoDto.setMyPurchase(allPurchase);
        infoDto.setMyDebt(allMyDebt);
        infoDto.setMyTrade(allTrade);
        infoDto.setTradersDebt(allTradeDebt);

        return new ApiResponse("FOUND", true, infoDto);
    }

    public ApiResponse getInfoByBranch(UUID branchId) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()) {
            return new ApiResponse("Branch Not Found", false);
        }
        List<Purchase> purchaseList = purchaseRepository.findAllByBranch_Id(branchId);
        if (purchaseList.isEmpty()){
            return new ApiResponse("Purchased Product Not Found");
        }

        double allPurchase = 0;
        double allMyDebt = 0;
        double allTrade = 0;
        double allTradeDebt = 0;
        for (Purchase purchase : purchaseList) {
            allPurchase += purchase.getTotalSum();
            allMyDebt += purchase.getDebtSum();
        }

        List<Trade> tradeList = tradeRepository.findAllByBranch_Id(branchId);
//        if (tradeList.isEmpty()) return new ApiResponse("NOT FOUND", false);
        for (Trade trade : tradeList) {
            allTrade += trade.getTotalSum();
            allTradeDebt += trade.getDebtSum();
        }
        InfoDto infoDto = new InfoDto();
        infoDto.setMyPurchase(allPurchase);
        infoDto.setMyDebt(allMyDebt);
        infoDto.setMyTrade(allTrade);
        infoDto.setTradersDebt(allTradeDebt);

        List<Outlay> outlayList = outlayRepository.findAllByBranch_Id(branchId);
        double totalOutlay = 0;
        if (!outlayList.isEmpty()){
            for (Outlay outlay : outlayList) {
                totalOutlay += outlay.getTotalSum();
            }
            infoDto.setMyOutlay(totalOutlay);
        }
        return new ApiResponse("FOUND", true, infoDto);
    }
}
