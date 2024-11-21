//package com.giwootjang.backend.cache;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import static com.mongodb.assertions.Assertions.assertFalse;
//import static com.mongodb.assertions.Assertions.assertTrue;
//
//@Component
//@RequiredArgsConstructor
//public class SquaredCalculator {
//    private final CacheHelper cache;
//
//    public int getSquareValueOfNumber(int input) {
//        if (cache.getSquareNumberCache().containsKey(input)) {
//            return cache.getSquareNumberCache().get(input);
//        }
//
//        System.out.println("Calculating square value of " + input +
//                " and caching result.");
//
//        int squaredValue = (int) Math.pow(input, 2);
//        cache.getSquareNumberCache().put(input, squaredValue);
//
//        return squaredValue;
//    }
//
//    //standard getters and setters;
//    public void whenCalculatingSquareValueAgain_thenCacheHasAllValues() {
//        for (int i = 10; i < 15; i++) {
//            assertFalse(cache.getSquareNumberCache().containsKey(i));
//            System.out.println("Square value of " + i + " is: "
//                    + getSquareValueOfNumber(i) + "\n");
//        }
//
//        for (int i = 10; i < 15; i++) {
//            assertTrue(cache.getSquareNumberCache().containsKey(i));
//            System.out.println("Square value of " + i + " is: "
//                    + getSquareValueOfNumber(i) + "\n");
//        }
//    }
//}