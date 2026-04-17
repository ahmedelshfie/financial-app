package com.example.finance.account.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link OperationType} enum.
 *
 * <p>Verifies the enum constants exist and that Jackson correctly deserialises JSON strings to the
 * corresponding constants (including case-sensitivity behaviour).
 */
@DisplayName("OperationType enum tests")
class OperationTypeTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  @DisplayName("DEBIT and CREDIT constants exist")
  void enumConstants_exist() {
    assertThat(OperationType.values()).containsExactly(OperationType.DEBIT, OperationType.CREDIT);
  }

  @Test
  @DisplayName("valueOf DEBIT returns DEBIT constant")
  void valueOf_debit_returnsDebitConstant() {
    assertThat(OperationType.valueOf("DEBIT")).isEqualTo(OperationType.DEBIT);
  }

  @Test
  @DisplayName("valueOf CREDIT returns CREDIT constant")
  void valueOf_credit_returnsCreditConstant() {
    assertThat(OperationType.valueOf("CREDIT")).isEqualTo(OperationType.CREDIT);
  }

  @Test
  @DisplayName("valueOf unrecognised string throws IllegalArgumentException")
  void valueOf_unknown_throwsIllegalArgumentException() {
    assertThatThrownBy(() -> OperationType.valueOf("TRANSFER"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("Jackson deserialises \"DEBIT\" to OperationType.DEBIT")
  void jackson_deserialise_debit() throws Exception {
    OperationType result = mapper.readValue("\"DEBIT\"", OperationType.class);
    assertThat(result).isEqualTo(OperationType.DEBIT);
  }

  @Test
  @DisplayName("Jackson deserialises \"CREDIT\" to OperationType.CREDIT")
  void jackson_deserialise_credit() throws Exception {
    OperationType result = mapper.readValue("\"CREDIT\"", OperationType.class);
    assertThat(result).isEqualTo(OperationType.CREDIT);
  }

  @Test
  @DisplayName("Jackson serialises DEBIT to \"DEBIT\"")
  void jackson_serialise_debit() throws Exception {
    String json = mapper.writeValueAsString(OperationType.DEBIT);
    assertThat(json).isEqualTo("\"DEBIT\"");
  }

  @Test
  @DisplayName("Jackson serialises CREDIT to \"CREDIT\"")
  void jackson_serialise_credit() throws Exception {
    String json = mapper.writeValueAsString(OperationType.CREDIT);
    assertThat(json).isEqualTo("\"CREDIT\"");
  }
}
