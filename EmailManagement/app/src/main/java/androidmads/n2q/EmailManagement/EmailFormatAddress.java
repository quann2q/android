package androidmads.n2q.EmailManagement;

public class EmailFormatAddress {

    public static String formatAddress (String address){
        address = address.trim();

        while (address.contains(" ")){
            address = address.replace(" ", "");
        }

        while (address.contains(",,")){
            address = address.replace(",,", ",");
        }

        StringBuilder stringBuilder = new StringBuilder(address);

        if (stringBuilder.charAt(0) == ','){
            stringBuilder.delete(0, 1);
        }

        if (stringBuilder.charAt(stringBuilder.length()-1) == ','){
            stringBuilder.delete(stringBuilder.length()-1, stringBuilder.length());
        }

        int i=0;
        while (i < stringBuilder.length()){
            if (stringBuilder.charAt(i) == ','){
                stringBuilder.insert(i+1, " ");
            }
            i++;
        }
        address = stringBuilder.toString();
        return address;
    }
}
