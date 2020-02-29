package com.wr.ty.grpc.util;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Signal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author xiaohei
 * @date 2020/2/19 0:45
 */
public class FluxUtil {

    private static final Flux<?> ON_COMPLETE_MARKER = Flux.error(new OnCompleteMarkerException());

    /**
     * Standard merge operator completes the stream only when all internal flux complete or there is an error.
     * In some scenarios we want to complete immediately, when any of the internal flux completes. This
     * method provides this functionality.
     */
    public static <T> Flux<T> mergeWhenAllActive(Flux<T>... flux) {
        List<Flux<T>> wrapped = new ArrayList<>(flux.length);
        for (int i = 0; i < flux.length; i++) {
            wrapped.add(flux[i].concatWith((Flux<T>) ON_COMPLETE_MARKER));
        }
        return Flux.merge(wrapped).onErrorResume(e -> {
            if (e instanceof OnCompleteMarkerException) {
                return Flux.empty();
            }
            return Flux.error(e);
        });
    }
    private static class OnCompleteMarkerException extends Exception {
    }
}
