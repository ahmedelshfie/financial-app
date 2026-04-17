package com.example.finance.account.service;

import com.example.finance.account.dto.AccountResponse;
import com.example.finance.account.dto.BalanceUpdateRequest;
import com.example.finance.account.dto.CreateAccountRequest;
import com.example.finance.account.exception.AccountNotFoundException;
import com.example.finance.account.exception.InsufficientFundsException;
import java.util.List;

/**
 * Business operations for managing bank accounts.
 *
 * <p>All write operations ({@link #create} and {@link #updateBalance}) run within a database
 * transaction managed by the implementation class. Read operations are executed in a read-only
 * transaction context for optimal query-plan selection.
 */
public interface AccountService {

  /**
   * Creates a new bank account for the specified customer.
   *
   * <p>The account is initialised with zero balances and {@code ACTIVE} status. A unique account
   * number is generated automatically.
   *
   * @param request the account creation request; must not be {@code null}
   * @return the persisted account details
   * @throws IllegalArgumentException if the requested account type code does not exist
   */
  AccountResponse create(CreateAccountRequest request);

  /**
   * Retrieves account details by its unique identifier.
   *
   * @param id the account's surrogate primary key; must not be {@code null}
   * @return the account details
   * @throws AccountNotFoundException if no account with the given {@code id} exists
   */
  AccountResponse getById(Long id);

  /**
   * Returns all accounts belonging to the specified customer.
   *
   * @param customerId the customer's unique identifier; must not be {@code null}
   * @return a (possibly empty) list of account details; never {@code null}
   */
  List<AccountResponse> findByCustomerId(Long customerId);

  /**
   * Applies a DEBIT or CREDIT operation to an account's balance.
   *
   * <p>Both {@code currentBalance} and {@code availableBalance} are updated atomically within a
   * single transaction.
   *
   * @param id the account's surrogate primary key; must not be {@code null}
   * @param request the balance update request containing the operation and amount
   * @return the updated account details
   * @throws AccountNotFoundException if no account with the given {@code id} exists
   * @throws InsufficientFundsException if the operation is DEBIT and the amount exceeds the
   *     available balance
   */
  AccountResponse updateBalance(Long id, BalanceUpdateRequest request);
}
