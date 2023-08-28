package ch.epfl.javelo;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.function.DoubleUnaryOperator;


/**
 * @author Upeski Stefan (330129)
 */
public final class Functions {

    public Functions(){};

    /**
     *
     * @param y of the function f(x) = y
     * @return a function
     */

    public static DoubleUnaryOperator constant(double y) {

        return new Constant(y);
    }

    private record Constant(double y) implements DoubleUnaryOperator {

        /**
         *
         * @param x is a value
         * @return the value a x of the function f(x)
         */
        @Override
        public double applyAsDouble(double x) {

            return y();
        }
    }

    public static DoubleUnaryOperator sampled(float[] samples, double xMax) {
        return new Sampled(samples,xMax);
    }

    private record Sampled(float[] samples, double xMax) implements DoubleUnaryOperator {
        /**
         *
         * @param samples array of float
         * @param xMax int that represent the xMax
         * @throws IllegalArgumentException if the length is smaller than 2 and xMax =0;
         */
        Sampled{
            Preconditions.checkArgument(samples.length >=2 && xMax>0);
        }


        /**
         * Mathematical function
         * @param x is a value
         * @return the value a x of f(x) ( the function )
         */
        @Override
        public double applyAsDouble(double x) {
            /**
             * donne Xmax / length = la longueur de l'intervalle
             */
            double function;

            // numberInterval découper la distance entre 0 et xMax par le nombre d'échantillon
            double numberInterval =  xMax/(samples.length-1);

            double pourcentage = x/ numberInterval;


            if(x <=0){
                return  samples[0];
            }

             if (x >=xMax){
                return samples[samples.length-1];
            }

             function = Math2.interpolate(
                    samples[(int) Math.floor(pourcentage)],
                    samples[(int) Math.floor(pourcentage)+1],
                    Math.abs(Math.floor(pourcentage) -pourcentage));
            return function;
        }
    }
}