package uz.pdp.springsecurity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.*;
import uz.pdp.springsecurity.payload.*;
import uz.pdp.springsecurity.repository.*;

import java.util.*;

@Service
public class ReportsService {


    @Autowired
    BusinessRepository businessRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    BranchRepository branchRepository;

    @Autowired
    TradeProductRepository tradeProductRepository;

    @Autowired
    PurchaseRepository purchaseRepository;

    @Autowired
    PurchaseProductRepository purchaseProductRepository;

    @Autowired
    OutlayRepository outlayRepository;

    public ApiResponse allProductAmount(UUID branchId){

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);

        if (optionalBranch.isEmpty()){
            return new ApiResponse("Branch Not Found",false);
        }

        Optional<Business> optionalBusiness = businessRepository.findById(optionalBranch.get().getBusiness().getId());

        if (optionalBusiness.isEmpty()){
            return new ApiResponse("Business Not Found",false);
        }

        UUID businessId = optionalBranch.get().getBusiness().getId();
        List<Product> productList = productRepository.findAllByBusiness_IdAndActiveTrue(businessId);

        if (productList.isEmpty()){
            return new ApiResponse("No Found Products");
        }

        double SumBySalePrice = 0D;
        double SumByBuyPrice = 0D;

        List<ProductReportDto> productReportDtoList=new ArrayList<>();
        ProductReportDto productReportDto=new ProductReportDto();
        for (Product product : productList) {
            productReportDto=new ProductReportDto();
            productReportDto.setName(product.getName());
            productReportDto.setBrand(product.getBrand().getName());
            productReportDto.setBranch(optionalBranch.get().getName());
            productReportDto.setCategory(product.getCategory().getName());
            productReportDto.setBuyPrice(product.getBuyPrice());
            productReportDto.setSalePrice(product.getSalePrice());

            Optional<Warehouse> optionalWarehouse = warehouseRepository.findByProductId(product.getId());
            Warehouse warehouse = new Warehouse();
            if (optionalWarehouse.isPresent()) {
                warehouse = optionalWarehouse.get();
            }
            productReportDto.setAmount(warehouse.getAmount());

            double amount = warehouse.getAmount();
            double salePrice = product.getSalePrice();
            double buyPrice = product.getBuyPrice();

            SumBySalePrice = amount * salePrice;
            SumByBuyPrice = amount * buyPrice;
            productReportDto.setSumBySalePrice(SumBySalePrice);
            productReportDto.setSumByBuyPrice(SumByBuyPrice);
            productReportDtoList.add(productReportDto);
        }

        return new ApiResponse("Business Products Amount" , true , productReportDtoList);
    }

    public ApiResponse allProductAmountByBranch(UUID branchId) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);


        if (optionalBranch.isEmpty()){
            return new ApiResponse("Branch Not Found",false);
        }

        Optional<Business> optionalBusiness = businessRepository.findById(optionalBranch.get().getBusiness().getId());

        if (optionalBusiness.isEmpty()){
            return new ApiResponse("Business Not Found",false);
        }

        UUID businessId = optionalBranch.get().getBusiness().getId();
        List<Product> productList = productRepository.findAllByBusiness_IdAndActiveTrue(businessId);

        if (productList.isEmpty()){
            return new ApiResponse("No Found Products");
        }

        double totalSumBySalePrice = 0D;
        double totalSumByBuyPrice = 0D;
        Amount amounts=new Amount();
        for (Product product : productList) {
            Optional<Warehouse> optionalWarehouse = warehouseRepository.findByProductId(product.getId());
            if (optionalWarehouse.isEmpty()){
                return new ApiResponse("No Found Product Amount");
            }
            Warehouse warehouse = optionalWarehouse.get();

            double amount = warehouse.getAmount();
            double salePrice = product.getSalePrice();
            double buyPrice = product.getBuyPrice();

            totalSumBySalePrice += amount * salePrice;
            totalSumByBuyPrice += amount * buyPrice;
            amounts.setTotalSumBySalePrice(totalSumBySalePrice);
            amounts.setTotalSumByBuyPrice(totalSumByBuyPrice);
        }

        return new ApiResponse("Business Products Amount" , true,amounts);
    }


    public ApiResponse mostSaleProducts(UUID branchId){

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()){
            return new ApiResponse("Branch Not Found");
        }

        Business business = optionalBranch.get().getBusiness();
        List<TradeProduct> tradeProductList = tradeProductRepository.findAllByProduct_BusinessId(business.getId());

        if (tradeProductList.isEmpty()){
            return new ApiResponse("Traded Product Not Found");
        }

        List<MostSaleProductsDto> mostSaleProductsDtoList=new ArrayList<>();

        for (int i = 0; i < tradeProductList.size(); i++) {

            MostSaleProductsDto mostSaleProductsDto=new MostSaleProductsDto();

            for (int i1 = 0; i1 < tradeProductList.size(); i1++) {

                if (tradeProductList.get(i).getProduct().getId().equals(tradeProductList.get(i1).getProduct().getId())){

                    mostSaleProductsDto.setName(tradeProductList.get(i).getProduct().getName());
                    mostSaleProductsDto.setBarcode(tradeProductList.get(i).getProduct().getBarcode());
                    mostSaleProductsDto.setMeasurement(tradeProductList.get(i).getProduct().getMeasurement().getName());
                    double amount = mostSaleProductsDto.getAmount();
                    mostSaleProductsDto.setAmount(amount += tradeProductList.get(i).getTradedQuantity());
                    mostSaleProductsDtoList.add(mostSaleProductsDto);
                }

                MostSaleProductsDto mostSaleProductsDtos=new MostSaleProductsDto();
                mostSaleProductsDto.setName(tradeProductList.get(i).getProduct().getName());
                mostSaleProductsDto.setBarcode(tradeProductList.get(i).getProduct().getBarcode());
                mostSaleProductsDto.setMeasurement(tradeProductList.get(i).getProduct().getMeasurement().getName());
                mostSaleProductsDto.setAmount(tradeProductList.get(i).getTradedQuantity());
                mostSaleProductsDtoList.add(mostSaleProductsDtos);
            }
        }
        mostSaleProductsDtoList.sort(Comparator.comparing(MostSaleProductsDto::getAmount).reversed());
        return new ApiResponse("Found",true,mostSaleProductsDtoList);
    }

    public ApiResponse findByName(UUID branchId,String name){

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()){
            return new ApiResponse("Not Found");
        }
        Business business = optionalBranch.get().getBusiness();
        List<Product> productList = productRepository.findAllByNameAndBusinessId(name, business.getId());
        if (productList.isEmpty()){
            return new ApiResponse("Not Found",false);
        }
        return new ApiResponse("Found",true,productList);
    }

    public ApiResponse purchaseReports(UUID branchId) {

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()){
            return new ApiResponse("Branch Not Found");
        }
        Branch branch = optionalBranch.get();

        List<PurchaseProduct> purchaseProductList = purchaseProductRepository.findAllByPurchase_BranchId(branch.getId());

        if (purchaseProductList.isEmpty()){
            return new ApiResponse("Purchase Product Not Found");
        }

        List<PurchaseReportsDto> purchaseReportsDtoList=new ArrayList<>();

        for (PurchaseProduct purchaseProduct : purchaseProductList) {
            PurchaseReportsDto purchaseReportsDto=new PurchaseReportsDto();
            purchaseReportsDto.setName(purchaseProduct.getProduct().getName());
            purchaseReportsDto.setPurchasedAmount(purchaseProduct.getPurchasedQuantity());
            purchaseReportsDto.setBuyPrice(purchaseProduct.getBuyPrice());
            purchaseReportsDto.setBarcode(purchaseProduct.getProduct().getBarcode());
            purchaseReportsDto.setTax(purchaseProduct.getProduct().getTax());
            purchaseReportsDto.setTotalSum(purchaseProduct.getTotalSum());
            purchaseReportsDto.setPurchasedDate(purchaseProduct.getCreatedAt());
            purchaseReportsDto.setSupplier(purchaseProduct.getPurchase().getSupplier().getName());
            purchaseReportsDto.setDebt(purchaseProduct.getPurchase().getDebtSum());
            purchaseReportsDtoList.add(purchaseReportsDto);
        }


        return new ApiResponse("Found",true,purchaseReportsDtoList);
    }

    public ApiResponse deliveryPriceGet(UUID branchId){

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()){
            return new ApiResponse("Not Found");
        }

        List<Purchase> purchaseList = purchaseRepository.findAllByBranch_Id(branchId);

        if (purchaseList.isEmpty()){
            return new ApiResponse("Not Found Purchase",false);
        }

        double totalDelivery = 0;
        for (Purchase purchase : purchaseList) {
            totalDelivery += purchase.getDeliveryPrice();
        }
        return new ApiResponse("Found",true, totalDelivery);
    }

    public ApiResponse outlayReports(UUID branchId){

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()){
            return new ApiResponse("Not Found");
        }
        List<Outlay> outlayList = outlayRepository.findAllByBranch_Id(branchId);
        if (outlayList.isEmpty()){
            return new ApiResponse("Not Found Outlay");
        }
        outlayList.sort(Comparator.comparing(Outlay::getTotalSum));
        return new ApiResponse("Found",true, outlayList);
    }

    public ApiResponse customerReports(UUID branchId){

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);
        if (optionalBranch.isEmpty()){
            return new ApiResponse("Not Found");
        }

        List<TradeProduct> tradeProductList = tradeProductRepository.findAllByProduct_BranchId(branchId);

        if (tradeProductList.isEmpty()){
            return new ApiResponse("Not Found Purchase",false);
        }

        List<CustomerReportsDto> customerReportsDtoList=new ArrayList<>();
        for (TradeProduct tradeProduct : tradeProductList) {
            CustomerReportsDto customerReportsDto=new CustomerReportsDto();
            customerReportsDto.setCustomerName(tradeProduct.getTrade().getCustomer().getName());
            customerReportsDto.setDate(tradeProduct.getTrade().getPayDate());
            customerReportsDto.setDebt(tradeProduct.getTrade().getDebtSum());
            customerReportsDto.setProduct(tradeProduct.getProduct().getName());
            customerReportsDto.setPaidSum(tradeProduct.getTrade().getPaidSum());
            customerReportsDto.setTradedQuantity(tradeProduct.getTradedQuantity());
            customerReportsDto.setBranchName(tradeProduct.getTrade().getBranch().getName());
            customerReportsDto.setTotalSum(tradeProduct.getTrade().getTotalSum());
            customerReportsDto.setPayMethod(tradeProduct.getTrade().getPayMethod().getType());
            customerReportsDto.setPaymentStatus(tradeProduct.getTrade().getPaymentStatus().getStatus());
            customerReportsDtoList.add(customerReportsDto);
        }
        customerReportsDtoList.sort(Comparator.comparing(CustomerReportsDto::getTotalSum).reversed());

        return new ApiResponse("Found",true,customerReportsDtoList);
    }


}
