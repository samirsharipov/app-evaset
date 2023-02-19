package uz.pdp.springsecurity.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.pdp.springsecurity.entity.Product;
import uz.pdp.springsecurity.entity.TradeProduct;

import javax.swing.text.html.Option;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TradeProductRepository extends JpaRepository<TradeProduct, UUID> {
  List<TradeProduct> findAllByProduct_Id(UUID product_id);
  List<TradeProduct> findAllByProduct_CategoryId(UUID categoryId);
  List<TradeProduct> findAllByProduct_BrandId(UUID brandId);
  List<TradeProduct> findAllByTradeId(UUID tradeId);
  List<TradeProduct> findAllByProductBusiness_Id(UUID product_id);
  List<TradeProduct> findAllByProduct_BusinessId(UUID product_business_id);
  List<TradeProduct> findAllByProduct_BranchId(UUID product_branch_id);
  List<TradeProduct> findAllByTrade_CustomerId(UUID customerId);
  List<TradeProduct> findAllByTrade_PayDate(Date date);
//  @Query(
//          value = "select * from (SELECT * FROM TradeProduct tp WHERE u.status = 1)",
//          nativeQuery = true)
//  List<TradeProduct> findAllByProduct_BranchId();
  List<TradeProduct> findAllByProduct_Business_IdOrderByTradedQuantity(UUID product_business_id);

    TradeProduct findByProduct_Id(UUID key);
}

