package eu.mihkel.java.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by mihkel on 05/11/16.
 * <p>
 * Responsible executing suppliers and
 * validating that the supply chain is returning valid responses.
 *
 * Indented usecases are where you have multiple checks - e.g. validation -
 * and need to return a result at the end.
 */
public class IfBuilder<T> {

    private List<Supplier<T>> suppliers = new ArrayList<>();
    private List<T> results = new ArrayList<>();
    private Function<T, Boolean> failChecker = (result) -> result == null ? true : false;


    public void add(Supplier<T> logic) {
        suppliers.add(logic);
    }

    /**
     * Executes suppliers until. If one fails will return without executing rest of the result.
     * @return lastresult
     */
    public Optional<T> build() {
        T lastResult = null;

        for (Supplier<T> supplier : suppliers) {
            lastResult = supplier.get();
            results.add(lastResult);
            if(lastResultFailed())
                break;
        }

        return Optional.ofNullable(lastResult);
    }

    public Optional<T> lastResult() {
        return results.isEmpty() ? Optional.empty() : Optional.ofNullable(results.get(results.size() - 1));
    }

    private Boolean lastResultFailed() {
        if (results.isEmpty())
            return false;

        if(lastResult().isPresent())
            return failChecker.apply(lastResult().get());

        return true;
    }

    public void setFailChecker(Function<T, Boolean> failChecker) {
        this.failChecker = failChecker;
    }

    public boolean success() {
        return lastResult().isPresent() && !lastResultFailed();
    }

    public boolean failed() {
        return !success();
    }
}
