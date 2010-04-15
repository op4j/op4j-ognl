/*
 * =============================================================================
 * 
 *   Copyright (c) 2010, The OP4J team (http://www.op4j.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package org.op4j.ognl.functions;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ognl.OgnlException;

import org.apache.commons.lang.Validate;
import org.javaruntype.type.Type;
import org.javaruntype.type.Types;
import org.op4j.exceptions.ExecutionException;
import org.op4j.functions.ExecCtx;
import org.op4j.functions.Function;
import org.op4j.util.VarArgsUtil;


/**
 * <p>
 * Function hub class for functions that evaluate OGNL expressions and return their results.
 * </p>
 *  
 * @author Daniel Fern&aacute;ndez
 *
 * @since 1.0
 */
public final class FnOgnl<T,R> extends Function<T,R> {
    
    private static final OgnlExpressionMap parsedExpressionsByExpression = new OgnlExpressionMap();
    
    
    public static final String TARGET_VARIABLE_NAME = "target";
    public static final String PARAM_VARIABLE_NAME = "param";
    public static final String INDEX_VARIABLE_NAME = "index";


    
    
    
    private final Type<R> resultType;
    private final String ognlExpression;
    private final Object[] parameters;
    
    

    /**
     * <p>
     * Evaluates an OGNL expression which returns Object. This
     * expression can have parameters (specified as the varargs <tt>optionalParameters</tt>
     * method parameter).
     * </p>
     * <p>
     * The following predefined variables can be used inside the expression:
     * </p>
     * <ul>
     *   <li><tt>#target</tt>: the target object (the target object is also the root of the expression)</li>
     *   <li><tt>#param</tt>: the specified parameters, as an array (specific parameters are thus accessible with "<tt>#param[0]</tt>", "<tt>#param[1]</tt>", ...)</li>
     *   <li><tt>#index</tt>: if the expression is executed during the iteration of an array, list,
     *       map or set, this variable will hold the iteration index.</li>
     * </ul>
     * 
     * @param ognlExpression the OGNL expression
     * @param optionalParameters the optional parameters
     * @return the result of evaluating the expression
     */
    public static Function<Object,Object> evalForObject(final String ognlExpression, final Object... optionalParameters) {
        return new FnOgnl<Object,Object>(Types.OBJECT, ognlExpression, VarArgsUtil.asOptionalObjectArray(Object.class, optionalParameters));
    }

    
    /**
     * <p>
     * Evaluates an OGNL expression which returns an object of type <tt>R</tt>. This
     * expression can have parameters (specified as the varargs <tt>optionalParameters</tt>
     * method parameter).
     * </p>
     * <p>
     * The following predefined variables can be used inside the expression:
     * </p>
     * <ul>
     *   <li><tt>#target</tt>: the target object (the target object is also the root of the expression)</li>
     *   <li><tt>#param</tt>: the specified parameters, as an array (specific parameters are thus accessible with "<tt>#param[0]</tt>", "<tt>#param[1]</tt>", ...)</li>
     *   <li><tt>#index</tt>: if the expression is executed during the iteration of an array, list,
     *       map or set, this variable will hold the iteration index.</li>
     * </ul>
     *
     * @param resultType the type of the object returned by the expression
     * @param ognlExpression the OGNL expression
     * @param optionalParameters the optional parameters
     * @return the result of evaluating the expression
     */
    public static <R> Function<Object,R> evalFor(final Type<R> resultType, final String ognlExpression, final Object... optionalParameters) {
        return new FnOgnl<Object,R>(resultType, ognlExpression, VarArgsUtil.asOptionalObjectArray(Object.class, optionalParameters));
    }

    
    
    
    /**
     * <p>
     * Evaluates an OGNL expression which returns BigInteger. This
     * expression can have parameters (specified as the varargs <tt>optionalParameters</tt>
     * method parameter).
     * </p>
     * <p>
     * The following predefined variables can be used inside the expression:
     * </p>
     * <ul>
     *   <li><tt>#target</tt>: the target object (the target object is also the root of the expression)</li>
     *   <li><tt>#param</tt>: the specified parameters, as an array (specific parameters are thus accessible with "<tt>#param[0]</tt>", "<tt>#param[1]</tt>", ...)</li>
     *   <li><tt>#index</tt>: if the expression is executed during the iteration of an array, list,
     *       map or set, this variable will hold the iteration index.</li>
     * </ul>
     * 
     * @param ognlExpression the OGNL expression
     * @param optionalParameters the optional parameters
     * @return the result of evaluating the expression
     */
    public static Function<Object,BigInteger> evalForBigInteger(final String ognlExpression, final Object... optionalParameters) {
        return new FnOgnl<Object,BigInteger>(Types.BIG_INTEGER, ognlExpression, VarArgsUtil.asOptionalObjectArray(Object.class, optionalParameters));
    }
    
    
    /**
     * <p>
     * Evaluates an OGNL expression which returns BigDecimal. This
     * expression can have parameters (specified as the varargs <tt>optionalParameters</tt>
     * method parameter).
     * </p>
     * <p>
     * The following predefined variables can be used inside the expression:
     * </p>
     * <ul>
     *   <li><tt>#target</tt>: the target object (the target object is also the root of the expression)</li>
     *   <li><tt>#param</tt>: the specified parameters, as an array (specific parameters are thus accessible with "<tt>#param[0]</tt>", "<tt>#param[1]</tt>", ...)</li>
     *   <li><tt>#index</tt>: if the expression is executed during the iteration of an array, list,
     *       map or set, this variable will hold the iteration index.</li>
     * </ul>
     * 
     * @param ognlExpression the OGNL expression
     * @param optionalParameters the optional parameters
     * @return the result of evaluating the expression
     */
    public static Function<Object,BigDecimal> evalForBigDecimal(final String ognlExpression, final Object... optionalParameters) {
        return new FnOgnl<Object,BigDecimal>(Types.BIG_DECIMAL, ognlExpression, VarArgsUtil.asOptionalObjectArray(Object.class, optionalParameters));
    }
    
    
    /**
     * <p>
     * Evaluates an OGNL expression which returns Boolean. This
     * expression can have parameters (specified as the varargs <tt>optionalParameters</tt>
     * method parameter).
     * </p>
     * <p>
     * The following predefined variables can be used inside the expression:
     * </p>
     * <ul>
     *   <li><tt>#target</tt>: the target object (the target object is also the root of the expression)</li>
     *   <li><tt>#param</tt>: the specified parameters, as an array (specific parameters are thus accessible with "<tt>#param[0]</tt>", "<tt>#param[1]</tt>", ...)</li>
     *   <li><tt>#index</tt>: if the expression is executed during the iteration of an array, list,
     *       map or set, this variable will hold the iteration index.</li>
     * </ul>
     * 
     * @param ognlExpression the OGNL expression
     * @param optionalParameters the optional parameters
     * @return the result of evaluating the expression
     */
    public static Function<Object,Boolean> evalForBoolean(final String ognlExpression, final Object... optionalParameters) {
        return new FnOgnl<Object,Boolean>(Types.BOOLEAN, ognlExpression, VarArgsUtil.asOptionalObjectArray(Object.class, optionalParameters));
    }
    
    
    /**
     * <p>
     * Evaluates an OGNL expression which returns Byte. This
     * expression can have parameters (specified as the varargs <tt>optionalParameters</tt>
     * method parameter).
     * </p>
     * <p>
     * The following predefined variables can be used inside the expression:
     * </p>
     * <ul>
     *   <li><tt>#target</tt>: the target object (the target object is also the root of the expression)</li>
     *   <li><tt>#param</tt>: the specified parameters, as an array (specific parameters are thus accessible with "<tt>#param[0]</tt>", "<tt>#param[1]</tt>", ...)</li>
     *   <li><tt>#index</tt>: if the expression is executed during the iteration of an array, list,
     *       map or set, this variable will hold the iteration index.</li>
     * </ul>
     * 
     * @param ognlExpression the OGNL expression
     * @param optionalParameters the optional parameters
     * @return the result of evaluating the expression
     */
    public static Function<Object,Byte> evalForByte(final String ognlExpression, final Object... optionalParameters) {
        return new FnOgnl<Object,Byte>(Types.BYTE, ognlExpression, VarArgsUtil.asOptionalObjectArray(Object.class, optionalParameters));
    }
    
    
    /**
     * <p>
     * Evaluates an OGNL expression which returns Character. This
     * expression can have parameters (specified as the varargs <tt>optionalParameters</tt>
     * method parameter).
     * </p>
     * <p>
     * The following predefined variables can be used inside the expression:
     * </p>
     * <ul>
     *   <li><tt>#target</tt>: the target object (the target object is also the root of the expression)</li>
     *   <li><tt>#param</tt>: the specified parameters, as an array (specific parameters are thus accessible with "<tt>#param[0]</tt>", "<tt>#param[1]</tt>", ...)</li>
     *   <li><tt>#index</tt>: if the expression is executed during the iteration of an array, list,
     *       map or set, this variable will hold the iteration index.</li>
     * </ul>
     * 
     * @param ognlExpression the OGNL expression
     * @param optionalParameters the optional parameters
     * @return the result of evaluating the expression
     */
    public static Function<Object,Character> evalForCharacter(final String ognlExpression, final Object... optionalParameters) {
        return new FnOgnl<Object,Character>(Types.CHARACTER, ognlExpression, VarArgsUtil.asOptionalObjectArray(Object.class, optionalParameters));
    }
    
    
    /**
     * <p>
     * Evaluates an OGNL expression which returns Calendar. This
     * expression can have parameters (specified as the varargs <tt>optionalParameters</tt>
     * method parameter).
     * </p>
     * <p>
     * The following predefined variables can be used inside the expression:
     * </p>
     * <ul>
     *   <li><tt>#target</tt>: the target object (the target object is also the root of the expression)</li>
     *   <li><tt>#param</tt>: the specified parameters, as an array (specific parameters are thus accessible with "<tt>#param[0]</tt>", "<tt>#param[1]</tt>", ...)</li>
     *   <li><tt>#index</tt>: if the expression is executed during the iteration of an array, list,
     *       map or set, this variable will hold the iteration index.</li>
     * </ul>
     * 
     * @param ognlExpression the OGNL expression
     * @param optionalParameters the optional parameters
     * @return the result of evaluating the expression
     */
    public static Function<Object,Calendar> evalForCalendar(final String ognlExpression, final Object... optionalParameters) {
        return new FnOgnl<Object,Calendar>(Types.CALENDAR, ognlExpression, VarArgsUtil.asOptionalObjectArray(Object.class, optionalParameters));
    }
    
    
    /**
     * <p>
     * Evaluates an OGNL expression which returns Date. This
     * expression can have parameters (specified as the varargs <tt>optionalParameters</tt>
     * method parameter).
     * </p>
     * <p>
     * The following predefined variables can be used inside the expression:
     * </p>
     * <ul>
     *   <li><tt>#target</tt>: the target object (the target object is also the root of the expression)</li>
     *   <li><tt>#param</tt>: the specified parameters, as an array (specific parameters are thus accessible with "<tt>#param[0]</tt>", "<tt>#param[1]</tt>", ...)</li>
     *   <li><tt>#index</tt>: if the expression is executed during the iteration of an array, list,
     *       map or set, this variable will hold the iteration index.</li>
     * </ul>
     * 
     * @param ognlExpression the OGNL expression
     * @param optionalParameters the optional parameters
     * @return the result of evaluating the expression
     */
    public static Function<Object,Date> evalForDate(final String ognlExpression, final Object... optionalParameters) {
        return new FnOgnl<Object,Date>(Types.DATE, ognlExpression, VarArgsUtil.asOptionalObjectArray(Object.class, optionalParameters));
    }
    
    
    /**
     * <p>
     * Evaluates an OGNL expression which returns Double. This
     * expression can have parameters (specified as the varargs <tt>optionalParameters</tt>
     * method parameter).
     * </p>
     * <p>
     * The following predefined variables can be used inside the expression:
     * </p>
     * <ul>
     *   <li><tt>#target</tt>: the target object (the target object is also the root of the expression)</li>
     *   <li><tt>#param</tt>: the specified parameters, as an array (specific parameters are thus accessible with "<tt>#param[0]</tt>", "<tt>#param[1]</tt>", ...)</li>
     *   <li><tt>#index</tt>: if the expression is executed during the iteration of an array, list,
     *       map or set, this variable will hold the iteration index.</li>
     * </ul>
     * 
     * @param ognlExpression the OGNL expression
     * @param optionalParameters the optional parameters
     * @return the result of evaluating the expression
     */
    public static Function<Object,Double> evalForDouble(final String ognlExpression, final Object... optionalParameters) {
        return new FnOgnl<Object,Double>(Types.DOUBLE, ognlExpression, VarArgsUtil.asOptionalObjectArray(Object.class, optionalParameters));
    }
    
    
    /**
     * <p>
     * Evaluates an OGNL expression which returns Float. This
     * expression can have parameters (specified as the varargs <tt>optionalParameters</tt>
     * method parameter).
     * </p>
     * <p>
     * The following predefined variables can be used inside the expression:
     * </p>
     * <ul>
     *   <li><tt>#target</tt>: the target object (the target object is also the root of the expression)</li>
     *   <li><tt>#param</tt>: the specified parameters, as an array (specific parameters are thus accessible with "<tt>#param[0]</tt>", "<tt>#param[1]</tt>", ...)</li>
     *   <li><tt>#index</tt>: if the expression is executed during the iteration of an array, list,
     *       map or set, this variable will hold the iteration index.</li>
     * </ul>
     * 
     * @param ognlExpression the OGNL expression
     * @param optionalParameters the optional parameters
     * @return the result of evaluating the expression
     */
    public static Function<Object,Float> evalForFloat(final String ognlExpression, final Object... optionalParameters) {
        return new FnOgnl<Object,Float>(Types.FLOAT, ognlExpression, VarArgsUtil.asOptionalObjectArray(Object.class, optionalParameters));
    }
    
    
    /**
     * <p>
     * Evaluates an OGNL expression which returns Integer. This
     * expression can have parameters (specified as the varargs <tt>optionalParameters</tt>
     * method parameter).
     * </p>
     * <p>
     * The following predefined variables can be used inside the expression:
     * </p>
     * <ul>
     *   <li><tt>#target</tt>: the target object (the target object is also the root of the expression)</li>
     *   <li><tt>#param</tt>: the specified parameters, as an array (specific parameters are thus accessible with "<tt>#param[0]</tt>", "<tt>#param[1]</tt>", ...)</li>
     *   <li><tt>#index</tt>: if the expression is executed during the iteration of an array, list,
     *       map or set, this variable will hold the iteration index.</li>
     * </ul>
     * 
     * @param ognlExpression the OGNL expression
     * @param optionalParameters the optional parameters
     * @return the result of evaluating the expression
     */
    public static Function<Object,Integer> evalForInteger(final String ognlExpression, final Object... optionalParameters) {
        return new FnOgnl<Object,Integer>(Types.INTEGER, ognlExpression, VarArgsUtil.asOptionalObjectArray(Object.class, optionalParameters));
    }
    

    /**
     * <p>
     * Evaluates an OGNL expression which returns Long. This
     * expression can have parameters (specified as the varargs <tt>optionalParameters</tt>
     * method parameter).
     * </p>
     * <p>
     * The following predefined variables can be used inside the expression:
     * </p>
     * <ul>
     *   <li><tt>#target</tt>: the target object (the target object is also the root of the expression)</li>
     *   <li><tt>#param</tt>: the specified parameters, as an array (specific parameters are thus accessible with "<tt>#param[0]</tt>", "<tt>#param[1]</tt>", ...)</li>
     *   <li><tt>#index</tt>: if the expression is executed during the iteration of an array, list,
     *       map or set, this variable will hold the iteration index.</li>
     * </ul>
     * 
     * @param ognlExpression the OGNL expression
     * @param optionalParameters the optional parameters
     * @return the result of evaluating the expression
     */
    public static Function<Object,Long> evalForLong(final String ognlExpression, final Object... optionalParameters) {
        return new FnOgnl<Object,Long>(Types.LONG, ognlExpression, VarArgsUtil.asOptionalObjectArray(Object.class, optionalParameters));
    }
    
    
    /**
     * <p>
     * Evaluates an OGNL expression which returns Short. This
     * expression can have parameters (specified as the varargs <tt>optionalParameters</tt>
     * method parameter).
     * </p>
     * <p>
     * The following predefined variables can be used inside the expression:
     * </p>
     * <ul>
     *   <li><tt>#target</tt>: the target object (the target object is also the root of the expression)</li>
     *   <li><tt>#param</tt>: the specified parameters, as an array (specific parameters are thus accessible with "<tt>#param[0]</tt>", "<tt>#param[1]</tt>", ...)</li>
     *   <li><tt>#index</tt>: if the expression is executed during the iteration of an array, list,
     *       map or set, this variable will hold the iteration index.</li>
     * </ul>
     * 
     * @param ognlExpression the OGNL expression
     * @param optionalParameters the optional parameters
     * @return the result of evaluating the expression
     */
    public static Function<Object,Short> evalForShort(final String ognlExpression, final Object... optionalParameters) {
        return new FnOgnl<Object,Short>(Types.SHORT, ognlExpression, VarArgsUtil.asOptionalObjectArray(Object.class, optionalParameters));
    }
    
    
    /**
     * <p>
     * Evaluates an OGNL expression which returns String. This
     * expression can have parameters (specified as the varargs <tt>optionalParameters</tt>
     * method parameter).
     * </p>
     * <p>
     * The following predefined variables can be used inside the expression:
     * </p>
     * <ul>
     *   <li><tt>#target</tt>: the target object (the target object is also the root of the expression)</li>
     *   <li><tt>#param</tt>: the specified parameters, as an array (specific parameters are thus accessible with "<tt>#param[0]</tt>", "<tt>#param[1]</tt>", ...)</li>
     *   <li><tt>#index</tt>: if the expression is executed during the iteration of an array, list,
     *       map or set, this variable will hold the iteration index.</li>
     * </ul>
     * 
     * @param ognlExpression the OGNL expression
     * @param optionalParameters the optional parameters
     * @return the result of evaluating the expression
     */
    public static Function<Object,String> evalForString(final String ognlExpression, final Object... optionalParameters) {
        return new FnOgnl<Object,String>(Types.STRING, ognlExpression, VarArgsUtil.asOptionalObjectArray(Object.class, optionalParameters));
    }
    
    
    /**
     * <p>
     * Evaluates an OGNL expression which returns List&lt;String&gt;. This
     * expression can have parameters (specified as the varargs <tt>optionalParameters</tt>
     * method parameter).
     * </p>
     * <p>
     * The following predefined variables can be used inside the expression:
     * </p>
     * <ul>
     *   <li><tt>#target</tt>: the target object (the target object is also the root of the expression)</li>
     *   <li><tt>#param</tt>: the specified parameters, as an array (specific parameters are thus accessible with "<tt>#param[0]</tt>", "<tt>#param[1]</tt>", ...)</li>
     *   <li><tt>#index</tt>: if the expression is executed during the iteration of an array, list,
     *       map or set, this variable will hold the iteration index.</li>
     * </ul>
     * 
     * @param ognlExpression the OGNL expression
     * @param optionalParameters the optional parameters
     * @return the result of evaluating the expression
     */
    public static Function<Object,List<String>> evalForListOfString(final String ognlExpression, final Object... optionalParameters) {
        return new FnOgnl<Object,List<String>>(Types.LIST_OF_STRING, ognlExpression, VarArgsUtil.asOptionalObjectArray(Object.class,optionalParameters));
    }

    
    /**
     * <p>
     * Evaluates an OGNL expression which returns Set&lt;String&gt;. This
     * expression can have parameters (specified as the varargs <tt>optionalParameters</tt>
     * method parameter).
     * </p>
     * <p>
     * The following predefined variables can be used inside the expression:
     * </p>
     * <ul>
     *   <li><tt>#target</tt>: the target object (the target object is also the root of the expression)</li>
     *   <li><tt>#param</tt>: the specified parameters, as an array (specific parameters are thus accessible with "<tt>#param[0]</tt>", "<tt>#param[1]</tt>", ...)</li>
     *   <li><tt>#index</tt>: if the expression is executed during the iteration of an array, list,
     *       map or set, this variable will hold the iteration index.</li>
     * </ul>
     * 
     * @param ognlExpression the OGNL expression
     * @param optionalParameters the optional parameters
     * @return the result of evaluating the expression
     */
    public static Function<Object,Set<String>> evalForSetOfString(final String ognlExpression, final Object... optionalParameters) {
        return new FnOgnl<Object,Set<String>>(Types.SET_OF_STRING, ognlExpression, VarArgsUtil.asOptionalObjectArray(Object.class,optionalParameters));
    }
    
    
    /**
     * <p>
     * Evaluates an OGNL expression which returns String[]. This
     * expression can have parameters (specified as the varargs <tt>optionalParameters</tt>
     * method parameter).
     * </p>
     * <p>
     * The following predefined variables can be used inside the expression:
     * </p>
     * <ul>
     *   <li><tt>#target</tt>: the target object (the target object is also the root of the expression)</li>
     *   <li><tt>#param</tt>: the specified parameters, as an array (specific parameters are thus accessible with "<tt>#param[0]</tt>", "<tt>#param[1]</tt>", ...)</li>
     *   <li><tt>#index</tt>: if the expression is executed during the iteration of an array, list,
     *       map or set, this variable will hold the iteration index.</li>
     * </ul>
     * 
     * @param ognlExpression the OGNL expression
     * @param optionalParameters the optional parameters
     * @return the result of evaluating the expression
     */
    public static Function<Object,String[]> evalForArrayOfString(final String ognlExpression, final Object... optionalParameters) {
        return new FnOgnl<Object,String[]>(Types.ARRAY_OF_STRING, ognlExpression, VarArgsUtil.asOptionalObjectArray(Object.class,optionalParameters));
    }
    
    
    /**
     * <p>
     * Evaluates an OGNL expression which returns List&lt;R&gt;. This
     * expression can have parameters (specified as the varargs <tt>optionalParameters</tt>
     * method parameter).
     * </p>
     * <p>
     * The following predefined variables can be used inside the expression:
     * </p>
     * <ul>
     *   <li><tt>#target</tt>: the target object (the target object is also the root of the expression)</li>
     *   <li><tt>#param</tt>: the specified parameters, as an array (specific parameters are thus accessible with "<tt>#param[0]</tt>", "<tt>#param[1]</tt>", ...)</li>
     *   <li><tt>#index</tt>: if the expression is executed during the iteration of an array, list,
     *       map or set, this variable will hold the iteration index.</li>
     * </ul>
     * 
     * @param resultType the type of the resulting list components
     * @param ognlExpression the OGNL expression
     * @param optionalParameters the optional parameters
     * @return the result of evaluating the expression
     */
    public static <R> Function<Object,List<R>> evalForListOf(final Type<R> resultType, final String ognlExpression, final Object... optionalParameters) {
        return new FnOgnl<Object,List<R>>(Types.listOf(resultType), ognlExpression, VarArgsUtil.asOptionalObjectArray(Object.class,optionalParameters));
    }

    
    /**
     * <p>
     * Evaluates an OGNL expression which returns Set&lt;R&gt;. This
     * expression can have parameters (specified as the varargs <tt>optionalParameters</tt>
     * method parameter).
     * </p>
     * <p>
     * The following predefined variables can be used inside the expression:
     * </p>
     * <ul>
     *   <li><tt>#target</tt>: the target object (the target object is also the root of the expression)</li>
     *   <li><tt>#param</tt>: the specified parameters, as an array (specific parameters are thus accessible with "<tt>#param[0]</tt>", "<tt>#param[1]</tt>", ...)</li>
     *   <li><tt>#index</tt>: if the expression is executed during the iteration of an array, list,
     *       map or set, this variable will hold the iteration index.</li>
     * </ul>
     * 
     * @param resultType the type of the resulting set components
     * @param ognlExpression the OGNL expression
     * @param optionalParameters the optional parameters
     * @return the result of evaluating the expression
     */
    public static <R> Function<Object,Set<R>> evalForSetOf(final Type<R> resultType, final String ognlExpression, final Object... optionalParameters) {
        return new FnOgnl<Object,Set<R>>(Types.setOf(resultType), ognlExpression, VarArgsUtil.asOptionalObjectArray(Object.class,optionalParameters));
    }

    
    /**
     * <p>
     * Evaluates an OGNL expression which returns R[]. This
     * expression can have parameters (specified as the varargs <tt>optionalParameters</tt>
     * method parameter).
     * </p>
     * <p>
     * The following predefined variables can be used inside the expression:
     * </p>
     * <ul>
     *   <li><tt>#target</tt>: the target object (the target object is also the root of the expression)</li>
     *   <li><tt>#param</tt>: the specified parameters, as an array (specific parameters are thus accessible with "<tt>#param[0]</tt>", "<tt>#param[1]</tt>", ...)</li>
     *   <li><tt>#index</tt>: if the expression is executed during the iteration of an array, list,
     *       map or set, this variable will hold the iteration index.</li>
     * </ul>
     * 
     * @param resultType the type of the resulting array components
     * @param ognlExpression the OGNL expression
     * @param optionalParameters the optional parameters
     * @return the result of evaluating the expression
     */
    public static <R> Function<Object,R[]> evalForArrayOf(final Type<R> resultType, final String ognlExpression, final Object... optionalParameters) {
        return new FnOgnl<Object,R[]>(Types.arrayOf(resultType), ognlExpression, VarArgsUtil.asOptionalObjectArray(Object.class,optionalParameters));
    }
    
    
    
    
    
    private FnOgnl(final Type<R> resultType, final String ognlExpression, final Object[] parameters) {
    	Validate.notNull(resultType, "Result type cannot be null");
    	Validate.notNull(ognlExpression, "Expression cannot be null");
        this.resultType = resultType;
        this.ognlExpression = ognlExpression;
        this.parameters = parameters;
    }
    
    
    public R execute(final T input, final ExecCtx ctx) throws Exception {
        return evalOgnlExpression(this.resultType, this.ognlExpression, input, this.parameters, ctx);
    }
    




    
    
    @SuppressWarnings("unchecked")
    private static <X> X evalOgnlExpression(
            final Type<X> resultType, final String ognlExpression, final Object targetObject, final Object parametersObject, 
            final ExecCtx execCtx) {
        
        Object parsedExpression = parsedExpressionsByExpression.get(ognlExpression);
        
        final Class<? super X> resultClass = resultType.getRawClass();
        
        if (parsedExpression == null) {
            try {
                parsedExpression = ognl.Ognl.parseExpression(ognlExpression);
            } catch (OgnlException e) {
                throw new ExecutionException(e);
            }
            parsedExpressionsByExpression.put(ognlExpression,parsedExpression);
        }
        
        try {
            final Map<String,Object> ctx = new HashMap<String,Object>();
            ctx.put(TARGET_VARIABLE_NAME, targetObject);
            ctx.put(PARAM_VARIABLE_NAME, parametersObject);
            ctx.put(INDEX_VARIABLE_NAME, execCtx.getIndex());
            final Object result = ognl.Ognl.getValue(parsedExpression, ctx, targetObject);
            if (result != null && resultClass != null && !Object.class.equals(resultClass)) {
                if (!(resultClass.isAssignableFrom(result.getClass()))) {
                    throw new IllegalStateException("Result of expression \"" + ognlExpression + "\" is not " +
                            "assignable from class " + resultClass.getName());
                }
            }
            return (X) result;
        } catch (OgnlException e) {
            throw new ExecutionException(e);
        }
        
    }
    
    
    
}
