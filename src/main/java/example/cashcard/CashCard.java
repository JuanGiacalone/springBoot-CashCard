

package example.cashcard;

// Add this import
import org.springframework.data.annotation.Id;

record CashCard(@Id Long id, Double amount, String owner) {

    public Long getId() {
        return id;
    }


    public Double getAmount() {
        return amount;
    }
}
