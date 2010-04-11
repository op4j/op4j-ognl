package org.op4j;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import ognl.Ognl;

import org.apache.commons.lang.ArrayUtils;
import org.javaruntype.type.Types;
import org.junit.Before;
import org.junit.Test;
import org.op4j.functions.Function;
import org.op4j.ognl.functions.FnOgnl;

public class AssortedTests extends TestCase {

	private AssortedTestsData testUtils;

	@Override
	@Before
	public void setUp() throws Exception {
		this.testUtils = new AssortedTestsData();
	}


	@Test
	public void test1() {
		Integer[] data = this.testUtils.getIntegerArray(16);

		String[] result = Op.onArrayOf(Types.INTEGER, data)
		.forEach()
		.exec(Types.STRING, FnOgnl.evalForString("\"Value is \" + #target"))
		.get();

		for (int index = 0; index < data.length; index++) {						
			assertEquals("Value is " + data[index], 
					result[index]);
		}
	}

	@Test
	public void test2() {
		Integer[] data = this.testUtils.getIntegerArray(16);

		Integer[] result = Op.onArrayOf(Types.INTEGER, data)
		.forEach()
		.ifIndex(2, 4, 6, 10, 15)
		.exec(FnOgnl.evalForInteger("#target + 10"))
		.endIf()
		.endFor()
		.get();

		for (int index = 0; index < data.length; index++) {	
			assertEquals(
					ArrayUtils.contains((new int[] {2, 4, 6, 10, 15}), index) 
					? Integer.valueOf(data[index].intValue() + 10) : data[index], 
							result[index]);
		}		
	}


    
    @Test
    public void test3() throws Exception {
        assertEquals(Boolean.TRUE, Op.onListFor(10,11).all(FnOgnl.evalForBoolean("#target <= 11")).get());
    }

    
    @Test
    public void test4() throws Exception {
        assertEquals(Boolean.TRUE, Op.onListFor(10,11).any(FnOgnl.evalForBoolean("#target < 11")).get());
    }
        
    
    
    @Test
    public void test45() throws Exception {
    
        
        Function<Object,String> keyFn = FnOgnl.evalForString("'KEY: ' + #target");
        Function<Object,String> valueFn = FnOgnl.evalForString("'VALUE: ' + #target");
        
        String[] valuesArray = new String[] {"one", "two", "three", "one"};
        List<String> valuesList = Arrays.asList(valuesArray);
        Set<String> valuesSet = new LinkedHashSet<String>(valuesList);
        
        Map<String,String> mapArray = Op.on(valuesArray).toMap(keyFn, valueFn).get(); 
        Map<String,String> mapList = Op.on(valuesList).toMap(keyFn, valueFn).get(); 
        Map<String,String> mapSet = Op.on(valuesSet).toMap(keyFn, valueFn).get();
        
        assertEquals(3, mapArray.size());
        assertEquals("KEY: one", mapArray.keySet().iterator().next());
        assertEquals("VALUE: one", mapArray.get("KEY: one"));
        assertEquals("VALUE: two", mapArray.get("KEY: two"));
        assertEquals(mapArray, mapList);
        assertEquals(mapList, mapSet);

        Map<String,String[]> mapGroupArray = Op.on(valuesArray).toGroupMapOf(Types.STRING, keyFn, valueFn).get(); 
        Map<String,List<String>> mapGroupList = Op.on(valuesList).toGroupMap(keyFn, valueFn).get(); 
        Map<String,Set<String>> mapGroupSet = Op.on(valuesSet).toGroupMap(keyFn, valueFn).get();

        assertEquals(3, mapGroupArray.size());
        assertEquals(3, mapGroupList.size());
        assertEquals(3, mapGroupSet.size());
        assertEquals(2, mapGroupArray.get("KEY: one").length);
        assertEquals(2, mapGroupList.get("KEY: one").size());
        assertEquals(1, mapGroupSet.get("KEY: one").size());
        assertEquals(1, mapGroupArray.get("KEY: two").length);
        assertEquals(1, mapGroupList.get("KEY: two").size());
        assertEquals(1, mapGroupSet.get("KEY: two").size());
        assertEquals(String[].class, mapGroupArray.get("KEY: one").getClass());
        assertEquals(ArrayList.class, mapGroupList.get("KEY: one").getClass());
        assertEquals(LinkedHashSet.class, mapGroupSet.get("KEY: one").getClass());
        assertEquals("KEY: one", mapGroupArray.keySet().iterator().next());
        assertEquals("VALUE: one", mapGroupArray.get("KEY: one")[0]);
        assertEquals("VALUE: two", mapGroupArray.get("KEY: two")[0]);
        assertEquals(Arrays.asList(mapGroupArray.get("KEY: one")), mapGroupList.get("KEY: one"));
        assertEquals(Arrays.asList(mapGroupArray.get("KEY: two")), mapGroupList.get("KEY: two"));
        assertEquals(Arrays.asList(mapGroupArray.get("KEY: three")), mapGroupList.get("KEY: three"));
        assertEquals(mapGroupList.get("KEY: two"), new ArrayList<String>(mapGroupSet.get("KEY: two")));
        assertEquals(mapGroupList.get("KEY: three"), new ArrayList<String>(mapGroupSet.get("KEY: three")));
        
    }
    
}

