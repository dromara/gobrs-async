package com.gobrs.async.core.common.domain;

import com.gobrs.async.core.common.def.DefaultConfig;

/**
 * The type Any condition.
 *
 * @param <T> the type parameter T is the return result
 * @program: gobrs -async
 * @ClassName AnyCondition
 * @description:
 * @author: sizegang
 * @create: 2022 -09-29
 */
public class AnyConditionResult<T> {

    private Boolean state;

    private T result;

    /**
     * Instantiates a new Any condition.
     *
     * @param builder the builder
     */
    public AnyConditionResult(Builder<T> builder) {
        this.state = builder.state;
        this.result = builder.result;
    }

    /**
     * Builder builder.
     *
     * @return the builder
     */
    public static Builder builder() {
        return new Builder().setState(DefaultConfig.anyConditionState);
    }

    /**
     * Gets state.
     *
     * @return the state
     */
    public Boolean getState() {
        return state;
    }

    /**
     * Sets state.
     *
     * @param state the state
     */
    public void setState(Boolean state) {
        this.state = state;
    }

    /**
     * Gets result.
     *
     * @return the result
     */
    public T getResult() {
        return result;
    }

    /**
     * Sets result.
     *
     * @param result the result
     */
    public void setResult(T result) {
        this.result = result;
    }

    /**
     * The type Builder.
     *
     * @param <T> the type parameter
     */
    public static class Builder<T> {

        private Boolean state;

        private T result;

        /**
         * Gets state.
         *
         * @return the state
         */
        public Boolean getState() {
            return state;
        }

        /**
         * Sets state.
         *
         * @param state the state
         * @return the state
         */
        public Builder setState(Boolean state) {
            this.state = state;
            return this;
        }


        /**
         * Gets result.
         *
         * @return the result
         */
        public T getResult() {
            return result;
        }

        /**
         * Sets result.
         *
         * @param result the result
         * @return the result
         */
        public Builder setResult(T result) {
            this.result = result;
            return this;
        }

        /**
         * Build any condition.
         *
         * @return the any condition
         */
        public AnyConditionResult build() {
            return new AnyConditionResult(this);
        }


    }


}
