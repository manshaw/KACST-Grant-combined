package cz.covid19cz.nebojsa.utility;

public class Month {
    public static String getName(int month) {
        switch (month) {
            case 1:
                return "Jan";
            case 2:
                return "Feb";
            case 3:
                return "Mar";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "Jun";
            case 7:
                return "July";
            case 8:
                return "Aug";
            case 9:
                return "Sep";
            case 10:
                return "Oct";
            case 11:
                return "Nov";
            case 12:
                return "Dec";
            default:
                return null;
        }
    }

    public static String getNumber(String month) {
        switch (month.toLowerCase()) {
            case "jan":
                return "01";
            case "feb":
                return "02";
            case "mar":
                return "03";
            case "april":
                return "04";
            case "may":
                return "05";
            case "jun":
                return "06";
            case "july":
                return "07";
            case "aug":
                return "08";
            case "sep":
                return "09";
            case "oct":
                return "10";
            case "nov":
                return "11";
            case "dec":
                return "12";
            default:
                return null;
        }
    }

    public enum Name {
        Jan, Feb, March, April, May, June, July, Aug, Sep, Nov, Dec
    }
}
