package org.app.mydukan.utils;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Codespeak on 05-07-2016.
 */
public class Utils {

    //Email Validation pattern
    public static final String regEx = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";

    //Fragments Tags
    public static final String Login_Fragment = "Login_Fragment";
    public static final String SignUp_Fragment = "SignUp_Fragment";
    public static final String DemoPage_Fragment = "DemoPage_Fragment";

    private final static long MILLISECS_PER_DAY = 24 * 60 * 60 * 1000;
    DateFormat df =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public boolean isStringEmpty(String input) {
        Boolean valid = false;
        if (input == null) {
            valid = true;
        } else if (input.trim().length() == 0) {
            valid = true;
        }
        return valid;
    }

    public boolean isEmailValid(String email) {
        Boolean valid = false;
        Matcher matcher;
        if (email != null && email.length() > 0) {
            String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            Pattern filter = Pattern.compile(EMAIL_PATTERN);
            matcher = filter.matcher(email);
            if (matcher.matches()) {
                valid = true;
            }
        }
        return valid;
    }

    public boolean isPasswordValid(String password) {
        return password.length() > 6;
    }

    public boolean isNumeric(String number) {
        try {
            double d = Double.parseDouble(number);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public final boolean isValidMobile(CharSequence mobileNumber) {
        Boolean valid = false;
        if (mobileNumber.length() == 10) {
            Pattern pattern = Pattern.compile("[0-9]{10}");
            Matcher matcher = pattern.matcher(mobileNumber);
            if (matcher.matches()) {
                valid = true;
            }
        }
        return valid;
    }

    public boolean isValidName(String name) {
        return name.matches("[a-zA-Z .]+");
    }

    public boolean isValidIFSCCode(String ifscCode) {
        Pattern pattern = Pattern.compile("[A-Z]{4}");
        Matcher matcher = pattern.matcher(ifscCode);
        if (matcher.matches()) {
            return true;
        }
        return false;
    }

    public String encodeString(String s) {
        if (s == null)
            return null;
        try {
            byte[] data = s.getBytes("UTF-8");
            return Base64.encodeToString(data, Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String dateFormatter(long timeInMilliSeconds) {
        return dateFormatter(timeInMilliSeconds, "dd MMM yyyy");
    }

    public String dateFormatter(long timeInMilliSeconds, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String dateStr = sdf.format(new Date(timeInMilliSeconds));
        return dateStr;
    }

    public static String dateFormatter(String dateStr, String sourceFormat, String targetFormat) {
        String formattedDate = "";
        try {
            DateFormat originalFormat = new SimpleDateFormat(sourceFormat, Locale.ENGLISH);
            DateFormat resultFormat = new SimpleDateFormat(targetFormat);
            Date date = originalFormat.parse(dateStr);
            formattedDate = resultFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    public long getMonthStartInMillSec() {
        Calendar calendar = Calendar.getInstance();

        //clear everything except the month
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        return calendar.getTimeInMillis();
    }

    public String numberFormat(String number) {
        String[] suffix = new String[]{"", "K", "M", "B", "T"};
        int MAX_LENGTH = 4;
        String r = new DecimalFormat("##0E0").format(Long.valueOf(number));
        r = r.replaceAll("E[0-9]", suffix[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);
        while (r.length() > MAX_LENGTH || r.matches("[0-9]+\\.[a-z]")) {
            r = r.substring(0, r.length() - 2) + r.substring(r.length() - 1);
        }
        return r;
    }

    public String capitalize(String s) {
        if (s.length() == 0) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public String toCamelCase(String string) {
        String result = "";
        if (!isStringEmpty(string)) {
            String[] words = string.split("\\s");
            for (String s : words) {
                result = result + capitalize(s) + " ";
            }
        }
        return result;
    }

    public String capitalizefirst(String s) {
        if (s.length() == 0) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
    public String toSameCase(String string) {
        String result = "";
        if (!isStringEmpty(string)) {
            String[] words = string.split("\\s");
            for (String s : words) {
                result = result + capitalizefirst(s) + " ";
            }
        }
        return result;
    }

    public String getPriceFormat(String price) {
        try {
            price = price.replaceAll("[,]", "");
            String symbol = "\u20B9";
            Double dbl = new Double(price);
            double priceVal = dbl.doubleValue();
            int number = (int) priceVal;
            if (priceVal <= 0) {
                return symbol + " __";
            }
            if (priceVal > number) {
                DecimalFormat format = new DecimalFormat(".00");
                return symbol + " " + format.format(priceVal);
            } else {

                return symbol + " " + number;
            }

        } catch (Exception ex) {
        }
        return null;
    }

    public static String getCurrentdate() {
        DateFormat df =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date current_Date = new Date();
        try {
            current_Date =df.parse(String.valueOf(current_Date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return String.valueOf(current_Date);
    }

    public static String getDisplayTimeText(String text) throws ParseException {
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        calendar.setTime(sdf.parse(text));

        Calendar currentDate=Calendar.getInstance();
        if(currentDate.get(Calendar.YEAR)!=calendar.get(Calendar.YEAR)){
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd MMM yyyy 'at' h:mm a");
            return simpleDateFormat.format(calendar.getTime());
        }else if(currentDate.get(Calendar.DAY_OF_YEAR) != calendar.get(Calendar.DAY_OF_YEAR)){
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd MMM 'at' h:mm a");
            return simpleDateFormat.format(calendar.getTime());
        }
        else{
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("h:mm a");
            return simpleDateFormat.format(calendar.getTime());
        }
    }


}

