package foodflow.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses the options of the "restaurants" command: {@code --cuisine X} and/or {@code --district Y}.
 * Values may contain several words. Syntax only, no business rules.
 */
record FilterArguments(String cuisine, String district) {

    static FilterArguments parse(List<String> args) {
        String cuisine = null;
        String district = null;
        String option = null;
        List<String> words = new ArrayList<>();
        for (int i = 0; i <= args.size(); i++) {
            boolean end = i == args.size();
            if (end || args.get(i).startsWith("--")) {
                if (option != null) {
                    String value = String.join(" ", words);
                    if (option.equals("--cuisine")) {
                        cuisine = value;
                    } else {
                        district = value;
                    }
                }
                if (end) {
                    break;
                }
                option = args.get(i);
                if (!option.equals("--cuisine") && !option.equals("--district")) {
                    throw new IllegalArgumentException("Unknown option " + option + ". Use --cuisine and/or --district.");
                }
                words.clear();
            } else if (option != null) {
                words.add(args.get(i));
            } else {
                throw new IllegalArgumentException("Usage: restaurants [--cuisine X] [--district Y]");
            }
        }
        return new FilterArguments(cuisine, district);
    }
}

