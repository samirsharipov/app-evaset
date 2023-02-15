package uz.pdp.springsecurity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.springsecurity.entity.*;
import uz.pdp.springsecurity.payload.ApiResponse;
import uz.pdp.springsecurity.payload.ProductReportDto;
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

        double totalSumBySalePrice = 0D;
        double totalSumByBuyPrice = 0D;

        List<ProductReportDto> productReportDtoList=new ArrayList<>();
        ProductReportDto productReportDto=new ProductReportDto();
        for (Product product : productList) {
            productReportDto=new ProductReportDto();
            productReportDto.setName(product.getName());
            productReportDto.setBrand(product.getBrand().getName());
            productReportDto.setBranch(product.getBranch().getClass().getName());
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

        Optional<Business> optionalBusiness = businessRepository.findById(branchId);

        Optional<Branch> optionalBranch = branchRepository.findById(branchId);

        if (optionalBranch.isEmpty()){
            return new ApiResponse("Branch Not Found",false);
        }

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

        List<ProductReportDto> productReportDtoList=new ArrayList<>();
        for (Product product : productList) {
            ProductReportDto productReportDto=new ProductReportDto();
            productReportDto.setName(product.getName());
            productReportDto.setBrand(product.getBrand().getName());
            productReportDto.setBranch(product.getBranch().getClass().getName());
            productReportDto.setCategory(product.getCategory().getName());
            productReportDto.setBuyPrice(product.getBuyPrice());
            productReportDto.setSalePrice(product.getSalePrice());

            Optional<Warehouse> optionalWarehouse = warehouseRepository.findByProductId(product.getId());
            if (optionalWarehouse.isEmpty()){
                return new ApiResponse("No Found Product Amount");
            }
            Warehouse warehouse = optionalWarehouse.get();
            productReportDto.setAmount(warehouse.getAmount());


            double amount = warehouse.getAmount();
            double salePrice = product.getSalePrice();
            double buyPrice = product.getBuyPrice();


            totalSumBySalePrice += amount * salePrice;
            totalSumByBuyPrice += amount * buyPrice;

            productReportDtoList.add(productReportDto);
        }

        return new ApiResponse("Business Products Amount" , true , productReportDtoList);
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

        tradeProductList.sort(Comparator.comparing(TradeProduct::getTradedQuantity));

        return new ApiResponse("Found",true,tradeProductList);
    }


}
