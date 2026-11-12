package com.tfg.cultura.api.loans.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tfg.cultura.api.catalog.model.enumerators.ItemType;
import com.tfg.cultura.api.catalog.service.BoardGameService;
import com.tfg.cultura.api.catalog.service.BookService;
import com.tfg.cultura.api.catalog.service.MovieService;
import com.tfg.cultura.api.catalog.service.RolGameService;
import com.tfg.cultura.api.catalog.service.SeriesService;
import com.tfg.cultura.api.core.exception.NotFoundException;
import com.tfg.cultura.api.core.exception.UnathenticatedException;
import com.tfg.cultura.api.core.exception.UnauthorizedException;
import com.tfg.cultura.api.core.exception.ValidationException;
import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.dto.LoanResponse;
import com.tfg.cultura.api.loans.model.enumerators.LoanStatus;
import com.tfg.cultura.api.loans.repository.LoanRepository;
import com.tfg.cultura.api.users.jwt.CustomUserDetailsService;
import com.tfg.cultura.api.users.service.UserService;

@Service
public class LoanUpdateService extends LoanService {

    // SERVICES
    private final RolGameService rolGameService;
    private final BoardGameService boardGameService;
    private final BookService bookService;
    private final MovieService movieService;
    private final SeriesService seriesService;

    public LoanUpdateService(LoanRepository loanRepository, CustomUserDetailsService userDetailsService,
            UserService userService, RolGameService rolGameService, BoardGameService boardGameService,
            BookService bookService, MovieService movieService, SeriesService seriesService) {
        super(loanRepository, userDetailsService, userService);
        this.rolGameService = rolGameService;
        this.boardGameService = boardGameService;
        this.bookService = bookService;
        this.movieService = movieService;
        this.seriesService = seriesService;
    }


    private void validateNewStatus(LoanStatus oldStatus, LoanStatus newStatus, boolean isAdmin)
            throws ValidationException, UnauthorizedException {
        if (isAdmin) {
            validateNewStatusAdmin(oldStatus, newStatus);
        } else {
            validateNewStatusUser(oldStatus, newStatus);
        }
    }

    private void validateNewStatusUser(LoanStatus oldStatus, LoanStatus newStatus)
            throws ValidationException, UnauthorizedException {
        if (oldStatus != LoanStatus.REQUESTED)
            throw new UnauthorizedException("No tiene permiso para actualizar el estado del préstamo.");
        if (newStatus != LoanStatus.CANCELLED)
            throw new ValidationException(logger,
                    Map.of("status", "Un préstamo solicitado solo puede pasar a CANCELADO."));
    }

    private void validateNewStatusAdmin(LoanStatus oldStatus, LoanStatus newStatus) throws ValidationException {
        switch (oldStatus) {
            case REQUESTED:
                if (newStatus != LoanStatus.ACTIVE && newStatus != LoanStatus.REJECTED)
                    throw new ValidationException(logger,
                            Map.of("status", "Un préstamo solicitado solo puede pasar a ACTIVO o RECHAZADO."));
            case ACTIVE:
                if (newStatus != LoanStatus.RETURNED)
                    throw new ValidationException(logger,
                            Map.of("status", "Un préstamo activo solo puede pasar a ATRASADO o DEVUELTO."));
            case OVERDUE:
                if (newStatus != LoanStatus.RETURNED)
                    throw new ValidationException(logger,
                            Map.of("status", "Un préstamo atrasado solo puede pasar a DEVUELTO."));
            default:
                throw new ValidationException(logger,
                        Map.of("status", "Un préstamo rechazado, cancelado o devuelto no puede cambiar de estado."));
        }
    }

    private Integer getLoanDays(ItemType itemType, String itemId) throws NotFoundException {
        switch (itemType) {
            case BOOK:
                return bookService.findById(itemId).getLoanDays();
            case MOVIE:
                return movieService.findById(itemId).getLoanDays();
            case SERIES:
                return seriesService.findById(itemId).getLoanDays();
            case BOARDGAME:
                return boardGameService.findById(itemId).getLoanDays();
            case ROLGAME:
                return rolGameService.findById(itemId).getLoanDays();
            default:
                // RN-20: No hay préstamos de videojuegos
                throw new ValidationException(logger,
                        Map.of("status", "No se puede actualizar el estado de este préstamo."));
        }
    }

    private Loan updateLoanDates(Loan loan, LoanStatus newStatus, ItemType itemType) throws NotFoundException {
        LocalDate currentDate = LocalDate.now(ZoneId.of("Europe/Madrid"));
        switch (newStatus) {
            case LoanStatus.CANCELLED:
                loan.setCancelDate(LocalDate.now());
            case LoanStatus.REJECTED:
                loan.setRejectionDate(LocalDate.now());
            case LoanStatus.ACTIVE:
                Integer loanDays = getLoanDays(itemType, loan.getItemId());
                loan.setStartDate(currentDate);
                loan.setDueDate(currentDate.plusDays(loanDays));
            case LoanStatus.RETURNED:
                loan.setReturnDate(currentDate);
            default:
                // No se actualizan fechas para los estados REQUESTED y OVERDUE
                break;
        }
        return loan;
    }

    @Transactional
    public LoanResponse updateLoanStatus(String loanCode, LoanStatus newStatus)
            throws NotFoundException, UnauthorizedException, UnathenticatedException {
        Loan loan = findLoanByCode(loanCode);

        // Si no es un administrador, solo puede actualizar sus propios préstamos
        checkCurrentUserIsLoanOwner(loan);
        validateNewStatus(loan.getStatus(), newStatus, userService.currentUserIsAdmin());
        updateLoanDates(loan, newStatus, loan.getItemType());

        loan.setStatus(newStatus);
        Loan updatedLoan = loanRepository.save(loan);
        logger.info("Préstamo {} actualizado a estado {} por el usuario {}", loanCode, newStatus,
                userDetailsService.getCurrentUserDetails().getId());
        return toResponse(updatedLoan);
    }

}
