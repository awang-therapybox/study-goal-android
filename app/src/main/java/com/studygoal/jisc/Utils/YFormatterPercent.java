//package com.studygoal.jisc.Utils;
//
//import com.github.mikephil.charting.components.YAxis;
//import com.github.mikephil.charting.formatter.YAxisValueFormatter;
//
///**
// * Created by Philipp Jahoda on 20/09/15.
// * Default formatter used for formatting labels of the YAxis. Uses a DecimalFormat with
// * pre-calculated number of digits (depending on max and min value).
// */
//public class YFormatterPercent implements YAxisValueFormatter {
//
//    public YFormatterPercent() {}
//
//    @Override
//    public String getFormattedValue(float value, YAxis yAxis) {
//        // avoid memory allocations here (for performance)
//        return (int)value + "%";
//    }
//}
