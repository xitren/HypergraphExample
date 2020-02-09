/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.encephalon.Hypergraph.Bayesian;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gusev_a
 */
public class Potential {
    public String[] names = new String[0];
    private Integer[] dimensions = new Integer[0];
    private Map<String,Double> table = new HashMap();
    private final Object tablelock = new Object();
    
    public Potential(){
    }
    
    public Potential(double[] p_table, String dimension_name1){
        increaseDimension(dimension_name1);
        for (int i=0;i < p_table.length;i++) {
            try {
                setRecord(p_table[i], i);
            } catch (Exception ex) {
                Logger.getLogger(
                        Potential.class.getName()
                ).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public Potential(double[][] p_table, String dimension_name1, 
                                            String dimension_name2){
        increaseDimension(dimension_name1);
        increaseDimension(dimension_name2);
        for (int i=0;i < p_table.length;i++) {
            for (int j=0;j < p_table[i].length;j++) {
                try {
                    setRecord(p_table[i][j], i, j);
                } catch (Exception ex) {
                    Logger.getLogger(
                            Potential.class.getName()
                    ).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public Potential(double[][][] p_table, String dimension_name1, 
                        String dimension_name2, String dimension_name3){
        increaseDimension(dimension_name1);
        increaseDimension(dimension_name2);
        increaseDimension(dimension_name3);
        for (int i=0;i < p_table.length;i++) {
            for (int j=0;j < p_table[i].length;j++) {
                for (int k=0;k < p_table[i][j].length;k++) {
                    try {
                        setRecord(p_table[i][j][k], i, j, k);
                    } catch (Exception ex) {
                        Logger.getLogger(
                                Potential.class.getName()
                        ).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }
    
    public Potential(Potential other){
        this();
        table.putAll(other.table);
        names = other.names.clone();
        dimensions = other.dimensions.clone();
    }
    
    public Potential(String... dimensions_names){
        for (String dimension_name : dimensions_names) {
            increaseDimension(dimension_name);
        }
    }
    
    public double[][] reduceToLink(String dimension_name1,String dimension_name2) 
                                                            throws Exception{
        if (getDimensionByName(dimension_name1) == -1 )
            throw new Exception("Wrong dimension 1 name!");
        if (getDimensionByName(dimension_name2) == -1 )
            throw new Exception("Wrong dimension 2 name!");
        boolean set = true;
        while (set){
            set = false;
            for (String name : names) {
                if ( !name.equals(dimension_name1) 
                        && !name.equals(dimension_name2) ){
                    this.sumAlongDimension(name);
                    set = true;
                    break;
                }
            }
        }
        double[][] ret = new double[dimensions[0]][dimensions[1]];
        for (int i=0;i < dimensions[0];i++) {
            for (int j=0;j < dimensions[1];j++) {
                ret[i][j] = this.getRecord(i,j);
            }
        }
        return ret;
    }
    
    public int getDimension(){
        return dimensions.length;
    }
    
    private int getDimensionByName(String dimension_name){
        int i;
        for (i=0;i < dimensions.length;i++){
            if (names[i].equals(dimension_name))
                break;
        }
        if (i >= dimensions.length)
            return -1;
        return i;
    }
    
    public Potential sumAlongDimension(String new_name){
        synchronized (tablelock) {
            Integer field = getDimensionByName(new_name);
            if (field < 0)
                return this;
            Integer[] old_dimensions = dimensions;
            String[] old_names = names;
            dimensions = new Integer[old_dimensions.length-1];
            names = new String[old_names.length-1];
            Potential.deleteOneFromArray(old_dimensions, dimensions, field);
            Potential.deleteOneFromArray(old_names, names, field);
            Map<String,Double> old_table = table;
            table = new HashMap();
            for (Map.Entry<String,Double> pair : old_table.entrySet()) {
                Integer[] coord = Potential.getCoordinatesFromString(pair.getKey());
                Integer[] new_coord = new Integer[coord.length-1];
                Potential.deleteOneFromArray(coord, new_coord, field);
                if (!table.containsKey(Potential.getStringFromCoordinates(new_coord))) {
                    table.put(
                            Potential.getStringFromCoordinates(new_coord), 
                            pair.getValue()
                    );
                } else {
                    String key = Potential.getStringFromCoordinates(new_coord);
                    table.put(
                            key, 
                            table.get(key) + pair.getValue() 
                    );
                }
            }
            normalize();
        }
        return this;
    }
    
    public double[] getAlongDimension(String dim_name) throws Exception{
        Integer field = getDimensionByName(dim_name);
        if (field < 0)
            throw new Exception("Wrong dimension name!");
        double[] ge;
        Potential p;
        synchronized (tablelock) {
            p = this.clonePotential();
        }
        for (String name : names) {
            if (name.equals(dim_name))
                continue;
            p = p.sumAlongDimension(name);
        }
        p.normalize();
        ge = new double[p.dimensions[0]];
        for (int i=0;i < ge.length;i++) {
            ge[i] = p.getRecord(i);
        }
        return ge;
    }
    
    public Potential getPAlongDimension(String dim_name){
        Integer field = getDimensionByName(dim_name);
        Potential p;
        synchronized (tablelock) {
            p = this.clonePotential();
        }
        for (String name : names) {
            if (name.equals(dim_name))
                continue;
            p = p.sumAlongDimension(name);
        }
        p.normalize();
        return p;
    }
    
    private static int[] findDifferentDimensions(Potential a, Potential b) {
        int diff_cnt = 0;
        int diff_id[];
        diff_id = new int[a.names.length];
        boolean find;
        for (int i=0;i < a.names.length;i++) {
            String a_s = a.names[i];
            find = false;
            for (String b_s : b.names) {
                if (a_s.equals(b_s)) {
                    find = true;
                    break;
                }
            }
            if (!find) {
                diff_id[diff_cnt++] = i;
            }
        }
        int diff_ret[] = new int[diff_cnt];
        System.arraycopy(diff_id, 0, diff_ret, 0, diff_cnt);
        return diff_ret;
    }
    
    private static String[] concatinateStrings(String[] str1, String[] str2) {
        String[] ret = new String[str1.length + str2.length]; 
        System.arraycopy(str1, 0, ret, 0, str1.length);
        System.arraycopy(str2, 0, ret, str1.length, str2.length);
        return ret;
    }
    
    private static String[] removeSomeFromStrings(String[] str, int... inds) 
                                                            throws Exception{
        int ptr = 0;
        String[] ret = new String[str.length - inds.length]; 
        for (int i=0;i < str.length;i++){
            boolean f = true;
            for (int ind : inds){
                if (ind >= str.length)
                    throw new Exception("Wrong dimensions in operands!");
                if (i == ind) {
                    f = false;
                    break;
                }
            }
            if (f)
                ret[ptr++] = str[i];
        }
        return ret;
    }
    
    private static String[] tookSomeFromStrings(String[] str, int... inds) 
                                                            throws Exception{
        int ptr = 0;
        String[] ret = new String[inds.length]; 
        for (int i=0;i < str.length;i++){
            boolean f = false;
            for (int ind : inds){
                if (ind >= str.length)
                    throw new Exception("Wrong dimensions in operands!");
                if (i == ind) {
                    f = true;
                    break;
                }
            }
            if (f)
                ret[ptr++] = str[i];
        }
        return ret;
    }
    
    private static Integer[] tookSomeFromCoords(String[] dest, String[] src, 
                                        Integer[] inds){
        Integer[] ret = new Integer[dest.length]; 
        for (int i=0;i < dest.length;i++){
            for (int j=0;j < src.length;j++){
                if ( dest[i].equals(src[j]) ) {
                    ret[i] = inds[j];
                    break;
                }
            }
        }
        return ret;
    }
    
    public void normalize() {
        Double num = 0.;
        for (Map.Entry<String,Double> pair : table.entrySet()) {
            num += pair.getValue();
        }
        for (Map.Entry<String,Double> pair : table.entrySet()) {
            table.put(pair.getKey(), pair.getValue()/num);
        }
    }
    
    protected Potential clonePotential() {
        Potential fi3 = new Potential(this); 
        return fi3;
    }
    
    public static Potential multiply(Potential p_a, Potential p_b) {
        if ((p_a == null) && (p_b != null))
            return p_b.clonePotential();
        if ((p_a != null) && (p_b == null))
            return p_a.clonePotential();
        if (p_a.getDimension() == 0)
            return p_b.clonePotential();
        if (p_b.getDimension() == 0)
            return p_a.clonePotential();
//        System.out.println("p_a");
//        System.out.println(p_a.toString());
//        System.out.println("p_b");
//        System.out.println(p_b.toString());
        int[] f1 = findDifferentDimensions(p_a,p_b);
        int[] f2 = findDifferentDimensions(p_b,p_a);
        Potential fi3 = null;
        try {
            String[] r1 = tookSomeFromStrings(p_a.names,f1);
            String[] r2 = tookSomeFromStrings(p_b.names,f2);
            String[] r3 = removeSomeFromStrings(p_b.names,f2);
            String[] ss = concatinateStrings(r1,r2);
            ss = concatinateStrings(ss,r3);
            Integer[] ssn = getSizes(ss,p_a,p_b);
            SpecialCounter sc = new SpecialCounter(ssn);
            fi3 = new Potential(ss); 
            for (int j=0;j < ss.length;j++){
                fi3.increaseDimension(ss[j]);
            }
            while (sc.hasNext()) {
                Integer[] ii = sc.next();
                fi3.setRecord(
                        p_a.getRecordOverDimension(ss, ii) 
                        * p_b.getRecordOverDimension(ss, ii), 
                        ii
                );
            }
            fi3.normalize();
        } catch (Exception ex) {
            Logger.getLogger(
                    Potential.class.getName()
            ).log(Level.SEVERE, null, ex);
        }
        return fi3;
    }
    
    private static Integer[] getSizes(String[] ss, Potential p_a, Potential p_b) 
                                                            throws Exception {
        Integer[] ret = new Integer[ss.length];
        for (int i=0;i < ss.length;i++) {
            int ii = -1;
            int i1 = p_a.getDimensionByName(ss[i]);
            int i2 = p_b.getDimensionByName(ss[i]);
            if (i1 >= 0)
                ii = p_a.dimensions[i1];
            else if (i2 >= 0)
                ii = p_b.dimensions[i2];
            if (ii == -1)
                throw new Exception("Wrong dimensions in operands!");
            ret[i] = ii;
        }
        return ret;
    }
    
    public int reduceDimension(String new_name) throws Exception {
        return reduceDimension(new_name, 0);
    }
    
    public int reduceDimension(String new_name, Integer value) throws Exception {
        synchronized (tablelock) {
            Integer field = getDimensionByName(new_name);
            if (field < 0)
                throw new Exception("Wrong dimension name!");
            Integer[] old_dimensions = dimensions;
            String[] old_names = names;
            dimensions = new Integer[old_dimensions.length-1];
            names = new String[old_names.length-1];
            Potential.deleteOneFromArray(old_dimensions, dimensions, field);
            Potential.deleteOneFromArray(old_names, names, field);
            Map<String,Double> old_table = table;
            table = new HashMap();
            for (Map.Entry<String,Double> pair : old_table.entrySet()) {
                Integer[] coord = Potential.getCoordinatesFromString(pair.getKey());
                Integer[] new_coord = new Integer[coord.length-1];
                Potential.deleteOneFromArray(coord, new_coord, field);
                if (Objects.equals(coord[field], value))
                    table.put(
                            Potential.getStringFromCoordinates(new_coord), 
                            pair.getValue()
                    );
            }
        }
        return dimensions.length;
    }
    
    public final int increaseDimension(String new_name){
        if (this.getDimensionByName(new_name) != -1)
            return -1;
        synchronized (tablelock) {
            Integer[] old_dimensions = dimensions;
            String[] old_names = names;
            dimensions = new Integer[old_dimensions.length+1];
            names = new String[old_names.length+1];
            System.arraycopy(old_dimensions, 0, dimensions, 0, old_dimensions.length);
            System.arraycopy(old_names, 0, names, 0, old_dimensions.length);
            dimensions[old_dimensions.length] = 1;
            names[old_names.length] = new_name;
            Map<String,Double> old_table = table;
            table = new HashMap();
            for (Map.Entry<String,Double> pair : old_table.entrySet()) {
                String id = pair.getKey();
                id = id.substring(0,id.length()-1);
                id = id.concat(",0]");
                table.put(
                        id, 
                        pair.getValue()
                );
            }
        }
        return dimensions.length;
    }
    
    public Double getRecordOverDimension(String[] axises, Integer[] coordinates){
        Double ret;
        Integer[] found_coordinates = tookSomeFromCoords(
                this.names,
                axises, 
                coordinates
        );
        ret = getRecord(found_coordinates);
        return ret;
    }
    
    public Double getRecord(Integer... coordinates){
        Double ret;
        synchronized (tablelock) {
            int ret_dimension = dimensions.length - coordinates.length + 1;
            if (ret_dimension != 1)
                return null;
//	    System.out.println(Potential.getStringFromCoordinates(coordinates));
            ret = table.get(Potential.getStringFromCoordinates(coordinates));
	    if (ret == null)
//		return 0.;
		return null;
        }
        return ret;
    }
    
    public final void setRecord(Double value, Integer... coordinates) throws Exception {
        synchronized (tablelock) {
            if (coordinates.length != dimensions.length)
                throw new Exception("Wrong coordinates lenght!"); 
            for (int i=0;i < coordinates.length;i++){
                if (coordinates[i] > dimensions[i])
                    throw new Exception("Wrong coordinates!"); 
                if (Objects.equals(coordinates[i], dimensions[i])){
                    dimensions[i]++;
                }
            }
            table.put(Potential.getStringFromCoordinates(coordinates), value);
        }
    }
    
    private static void deleteOneFromArray(Object[] src, Object[] dest, int pos){
        for (int i=0,ptr=0;i < src.length;i++) {
            if (i != pos) {
                dest[ptr++] = src[i];
            }
        }
    }
    
    private static String getStringFromCoordinates(Integer... coordinates){
        String ret = "[";
        for (int i=0;i < coordinates.length;i++){
            if (i != 0)
                ret = ret.concat(",");
            ret = ret.concat(coordinates[i].toString());
        }
        ret = ret.concat("]");
        return ret;
    }
    
    private static Integer[] getCoordinatesFromString(String id){
        String str = id.substring(1, id.length()-1);
        String[] ints = str.split(",");
        Integer[] ret = new Integer[ints.length];
        for (int i=0;i < ints.length;i++){
            ret[i] = Integer.parseInt(ints[i]);
        }
        return ret;
    }
    
    @Override
    public String toString(){
        String str = "";
//        str = str.concat("========================================\n");
        str = str.concat("        Potential [");
        for (int i=0;i < names.length;i++){
            if (i != 0)
                str = str.concat(",");
            str = str.concat(names[i]);
        }
        str = str.concat("] :\n");
        for (Map.Entry<String,Double> pair : table.entrySet()) {
            str = str.concat("        "+pair.getKey()+" = "+pair.getValue()+"\n");
        }
//        str = str.concat("========================================\n");
        return str;
    }
    
    public String eachToString(){
        String str = "";
        for (int i=0;i < names.length;i++){
	    str = str.concat("        Potential ");
            str = str.concat(names[i]);
	    str = str.concat(":\n");
	    Potential my = this.getPAlongDimension(names[i]);
	    my.normalize();
	    for (Map.Entry<String,Double> pair : my.table.entrySet()) {
		str = str.concat("        "+pair.getKey()+" = "+pair.getValue()+"\n");
	    }
        }
        return str;
    }
}
