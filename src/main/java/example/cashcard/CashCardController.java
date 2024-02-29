package example.cashcard;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cashcards")
class CashCardController {
    private final CashCardRepository cashCardRepository;

    private CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<CashCard> findById(@PathVariable Long requestedId, Principal principal) {
        Optional<CashCard> cashCardOptional = Optional.ofNullable(cashCardRepository.findByIdAndOwner(requestedId, principal.getName()));
        return cashCardOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    //    @GetMapping()
//    private ResponseEntity<Iterable<CashCard>> findAll() {
//
//        return ResponseEntity.ok(cashCardRepository.findAll());
//    }
    @GetMapping
    private ResponseEntity<List<CashCard>> findAll(Pageable pageable, Principal principal) {
        Page<CashCard> page = cashCardRepository.findByOwner(principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        // HERE I USE A PROVIDED OR A DEFAULT SORTING : ASC BY AMOUNT
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                ));
        return ResponseEntity.ok(page.getContent());
    }

//    @PostMapping
//    private ResponseEntity<Void> save(@RequestBody CashCard newCashCardRequest) {
//        CashCard savedCashCard = cashCardRepository.save(newCashCardRequest);
//        URI locationOfNewCashCard = URI.create("/cashcards/" + savedCashCard.getId());
//        return ResponseEntity.created(locationOfNewCashCard).build();
//    }

    // This one uses owner right...
    @PostMapping
    private ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest, Principal principal) {
        CashCard cashCardWithOwner = new CashCard(null, newCashCardRequest.amount(), principal.getName());
        CashCard savedCashCard = cashCardRepository.save(cashCardWithOwner);
        URI locationOfNewCashCard = URI.create("/cashcards/" + savedCashCard.getId());
        return ResponseEntity.created(locationOfNewCashCard).build();
    }
}