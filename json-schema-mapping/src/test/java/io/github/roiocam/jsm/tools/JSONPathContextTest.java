/* (C)2025 */
package io.github.roiocam.jsm.tools;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.roiocam.jsm.facade.JSONPathContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

abstract class JSONPathContextTest {

    String data = "{\"is_disabled\":0}";

    abstract JSONPathContext getContext(String data);

    @Test
    public void legal_expr_case() {
        String expr = "?<$.is_disabled> {==0:active} {==-1:inactive} {unknown}";
        JSONPathContext context = getContext(data);
        String read = context.read(expr, String.class);
        assertEquals("active", read);
    }

    @Test
    public void illegal_expr_case_with_multiple_default() {
        String expr = "?<$.is_disabled> {==0:active} {==-1:inactive} {unknown} {unknown}";
        JSONPathContext context = getContext(data);
        try {
            context.read(expr, String.class);
        } catch (Exception e) {
            assertEquals(
                    "invalid expression: only one default mapping is allowed. current: 2",
                    e.getMessage());
        }
    }

    @Test
    public void illegal_expr_case_with_empty_conditional() {
        String expr = "?<$.is_disabled> {unknown}";
        JSONPathContext context = getContext(data);
        try {
            context.read(expr, String.class);
        } catch (Exception e) {
            assertEquals(
                    "invalid expression: at least one condition mapping is required",
                    e.getMessage());
        }
    }

    @Test
    public void illegal_expr_case_with_no_mapping() {
        String expr = "?<$.is_disabled>";
        JSONPathContext context = getContext(data);
        try {
            context.read(expr, String.class);
        } catch (Exception e) {
            assertEquals("invalid expression: no mappings", e.getMessage());
        }
    }

    @Test
    public void illegal_expr_case_with_no_jsonpath() {
        String expr = "{==0:active} {==-1:inactive} {unknown} {unknown}";
        JSONPathContext context = getContext(data);
        Assertions.assertThrows(
                Exception.class,
                () -> {
                    context.read(expr, String.class);
                });
    }
}
