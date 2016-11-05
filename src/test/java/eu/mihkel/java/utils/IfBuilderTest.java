package eu.mihkel.java.utils;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * Created by mihkel on 05/11/16.
 */
public class IfBuilderTest {

    private IfBuilder ifBuilder;
    private String value;

    @Before
    public void before() {
        ifBuilder = new IfBuilder();
        value = "Default Value";
    }

    @Test
    public void shouldReturnValue() throws Exception {
        ifBuilder.add(() -> value);
        Optional optional = ifBuilder.build();

        assertEquals(value, optional.get());
    }

    @Test
    public void shouldReturnNullValueNotLast() throws Exception {
        Function<String, Boolean> failChecker = (result) -> result == null ? true : false;
        assertTrue(failChecker.apply(null));
        ifBuilder.add(() -> value);
        ifBuilder.add(() -> null);
        ifBuilder.add(() -> value);
        Optional optional = ifBuilder.build();

        assertFalse(optional.isPresent());
    }

    @Test
    public void shouldReturnFalseWithSuppliedChecker() {
        Function<String, Boolean> failChecker = (s) -> s == null || s.length() < 3;

        assertFalse(failChecker.apply(value));

        ifBuilder.setFailChecker(failChecker);

        ifBuilder.add(() -> value);
        ifBuilder.add(() -> "");
        ifBuilder.build();

        assertFalse(ifBuilder.success());
    }

    @Test
    public void shouldReturnTrueWithSuppliedChecker() {
        Function<String, Boolean> failChecker = (s) -> s == null || s.length() < 3;

        ifBuilder.setFailChecker(failChecker);

        assertFalse(failChecker.apply(value));
        ifBuilder.add(() -> value);
        ifBuilder.add(() -> value);
        Optional optional = ifBuilder.build();

        assertTrue(optional.isPresent());
        assertEquals(value, optional.get());
    }

    @Test
    public void successMethodShouldReturnSuccess() {
        Function<String, Boolean> failChecker = (s) -> s == null || s.length() > 3;

        ifBuilder.setFailChecker(failChecker);

        ifBuilder.add(() -> "2");
        ifBuilder.add(() -> "");
        ifBuilder.build();
        assertTrue(ifBuilder.success());
        assertFalse(ifBuilder.failed());

    }

    @Test
    public void successMethodShouldReturnSuccessFail() {
        Function<String, Boolean> failChecker = (s) -> s == null || s.length() > 3;

        ifBuilder.setFailChecker(failChecker);

        ifBuilder.add(() -> value);
        ifBuilder.add(() -> value + "1");
        ifBuilder.build();
        assertFalse(ifBuilder.success());
        assertTrue(ifBuilder.failed());
    }

    @Test
    public void shouldReturnLastValue() {

        Function<String, Boolean> failChecker = (s) -> s == null || s.length() > 3;

        ifBuilder.setFailChecker(failChecker);

        ifBuilder.add(() -> "2");
        ifBuilder.add(() -> "23");
        value = "5";
        ifBuilder.add(() -> value);
        ifBuilder.build();
        assertEquals(value, ifBuilder.lastResult().get());
    }

    @Test
    public void shouldReturnFirstFailedValue() {

        Function<String, Boolean> failChecker = (s) -> s == null || s.length() > 3;

        ifBuilder.setFailChecker(failChecker);

        ifBuilder.add(() -> value);
        ifBuilder.add(() -> "2");
        ifBuilder.add(() -> "23");
        ifBuilder.build();

        assertTrue(ifBuilder.failed());
        assertEquals(value, ifBuilder.lastResult().get());
    }

}