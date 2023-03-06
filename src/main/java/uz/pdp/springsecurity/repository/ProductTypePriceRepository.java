package uz.pdp.springsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.springsecurity.entity.ProductTypePrice;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductTypePriceRepository extends JpaRepository<ProductTypePrice, UUID> {
    List<ProductTypePrice> findAllByProductId(UUID product_id);
    List<ProductTypePrice> findAllByProduct_BranchId(UUID product_branch_id);
    List<ProductTypePrice> findAllByProduct_CategoryIdAndProduct_BranchIdAndProduct_ActiveTrue(UUID product_category_id, UUID product_branch_id);

    List<ProductTypePrice> findAllByProduct_BrandIdAndProduct_BranchIdAndProduct_Active(UUID product_brand_id, UUID product_branch_id);

    List<ProductTypePrice> findAllByProduct_BrandIdAndProduct_CategoryIdAndProduct_BranchId(UUID product_brand_id, UUID product_category_id, UUID product_branch_id);
    List<ProductTypePrice> findAllByProduct_CategoryIdAndProduct_BranchId(UUID product_category_id, UUID product_branch_id);
    Optional<ProductTypePrice> findByProductId(UUID product_id);

    boolean existsByBarcodeAndProduct_BusinessId(String barcode, UUID businessId);
    boolean existsByBarcodeAndProduct_BusinessIdAndIdIsNot(String barcode, UUID businessId, UUID productTypePriceId);
}
