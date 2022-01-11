package com.example.majon.sort;

public class SortUtil {
  
    private SortUtil() {  
        super();  
    }  
  
    public static void bubbleSort(Byte[] array, boolean isAsc) {
        for (int i = 0; i < array.length - 1; i++) {  
            for (int j = i; j < array.length; j++) {  
                if (isAsc ? array[i] > array[j] : array[i] < array[j]) {  
                    Byte temp = array[j];
                    array[j] = array[i];  
                    array[i] = temp;  
                }  
            }  
        }  
    }  
  
    public static void quickSort(Byte[] array, int start, int end, boolean isAsc) {  
        if (start < end) {  
            int base = array[start];  
            int i = start;  
            int j = end;  
            Byte temp;
            while (i <= j) {  
                while (isAsc ? (array[i] > base) && (i < end) : (array[i] < base) && (i < end)) {  
                    i++;  
                }  
                while (isAsc ? (array[j] < base) && (j > start) : (array[j] > base) && (j > start)) {  
                    j--;  
                }  
                if (i <= j) {  
                    temp = array[j];  
                    array[j] = array[i];  
                    array[i] = temp;  
                    i++;  
                    j--;  
                }  
            }  
            if (start < j) {  
                quickSort(array, start, j, isAsc);  
            }  
            if (end > i) {  
                quickSort(array, i, end, isAsc);  
            }  
        }  
    }  
  
    public static void selectSort(Byte[] array, boolean isAsc) {  
        Byte temp;
        int index;  
        for (int i = 0; i < array.length; i++) {  
            index = i;  
            for (int j = i + 1; j < array.length; j++) {  
                if (isAsc ? array[index] > array[j] : array[index] < array[j]) {  
                    index = j;  
                }  
            }  
            temp = array[index];  
            array[index] = array[i];  
            array[i] = temp;  
        }  
    }  
  
    public static Byte[] insertSort(Byte[] array, boolean isAsc) {
        int index;
        Byte temp;
        for (int i = 1; i < array.length; i++) {  
            temp = array[i];  
            // temp > array[index - 1]时 就该将temp插入该位置了 不需要移位了  
            for (index = i; index > 0 && (isAsc ? temp < array[index - 1] : temp > array[index - 1]); index--) {  
                array[index] = array[index - 1];  
            }  
            array[index] = temp;  
        }
        return array;
    }  
  
    public static void mergeSort(Byte[] array, int left, int right, boolean isAsc) {  
        if (left < right) {  
            int center = (left + right) / 2;  
            mergeSort(array, left, center, isAsc);  
            mergeSort(array, center + 1, right, isAsc);  
            merge(array, left, center, right, isAsc);  
        }  
    }  
  
    private static void merge(Byte[] array, int left, int center, int right, boolean isAsc) {
        Byte tempArray[] = new Byte[array.length];
        int tempIndex = left;// 新数组下标
        int index = left;// 放回原数组时使用  
        int mid = center + 1;  
        while (left <= center && mid <= right) {  
            if (isAsc ? array[left] <= array[mid] : array[left] >= array[mid]) {  
                tempArray[tempIndex++] = array[left++];  
            } else {  
                tempArray[tempIndex++] = array[mid++];  
            }  
        }  
        while (left <= center) {  
            tempArray[tempIndex++] = array[left++];  
        }  
        while (mid <= right) {  
            tempArray[tempIndex++] = array[mid++];  
        }  
        while (index <= right) {  
            array[index] = tempArray[index];  
            index++;  
        }  
    }  
  
}  