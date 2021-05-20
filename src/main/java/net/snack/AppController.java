package net.snack;


import net.snack.dao.model.Category;
import net.snack.dao.model.Record;
import net.snack.service.CategoryService;
import net.snack.service.RecordService;
import net.snack.util.DateValidatorUsingDateFormat;
import org.apache.commons.validator.routines.DateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AppController {

    private final String ERROR = "error";
    private final String SUCCESS = "success";
    private final String MESSAGE = "message";

    private final String CATEGORIES = "categories";
    private final String RECORDS = "records";

    private final String MAIN_PAGE = "index";
    private final String REDIRECT = "redirect:/";

    private final String ADD_CATEGORY = "addCategory";
    private final String ADD_ITEM = "addItem";
    private final String PURCHASE = "purchase";
    private final String LIST = "list";
    private final String CLEAR = "clear";
    private final String REPORT = "report";

    private final String SPACE = " ";
    private final String DOUBLE_QUOTES = "\"";
    private final String SPECIFIC_DOUBLE_QUOTES_OPEN = "“";
    private final String SPECIFIC_DOUBLE_QUOTES_CLOSE = "”";

    private final String DATE_FORMAT = "yyyy-MM-dd";
    private final String SHORT_DATE_FORMAT = "yyyy-MM";

    private final int ZERO_AMOUNT = 0;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
    private final SimpleDateFormat shortFormat = new SimpleDateFormat(SHORT_DATE_FORMAT);

    public static List<String> commands = new ArrayList<>();

    static {
        commands.add("addCategory");
        commands.add("addItem");
        commands.add("purchase");
        commands.add("list");
        commands.add("clear");
        commands.add("report");
    }

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RecordService recordService;

    @RequestMapping("/")
    public String viewHomePage() {
        return MAIN_PAGE;
    }

    @RequestMapping("/command")
    public String command(@ModelAttribute("command") String command,
                          RedirectAttributes redirectAttributes) {

        String[] values = command.split(SPACE);

        String action;

        if (values.length > 1) {
            action = command.substring(0, command.indexOf(SPACE));
        } else {
            action = command;
        }
        int lastIndex = values.length - 1;

        redirectAttributes.addAttribute("command", command);
        redirectAttributes.addAttribute("values", values);
        redirectAttributes.addAttribute("lastIndex", lastIndex);

        String redirectValue = MAIN_PAGE;

        if (commands.contains(action)) {
            switch (action) {
                case ADD_CATEGORY:
                    redirectValue = REDIRECT + ADD_CATEGORY;
                    break;
                case ADD_ITEM:
                    redirectValue = REDIRECT + ADD_ITEM;
                    break;
                case PURCHASE:
                    redirectValue = REDIRECT + PURCHASE;
                    break;
                case LIST:
                    redirectValue = REDIRECT + LIST;
                    break;
                case CLEAR:
                    redirectValue = REDIRECT + CLEAR;
                    break;
                case REPORT:
                    redirectValue = REDIRECT + REPORT;
                    break;
            }
        }
        return redirectValue;
    }

    @RequestMapping(value = "/addCategory")
    public String addCategory(@ModelAttribute("command") String command,
                             @ModelAttribute("lastIndex") Integer lastIndex,
                             @ModelAttribute("values") String[] values,
                             Model model) {

        try {
            String name = parseName(command);
            Double price = Double.valueOf(values[lastIndex - 1]);
            Integer amount = Integer.valueOf(values[lastIndex]);

            Category categoryByName = categoryService.findByName(name);

            if (categoryByName != null) {
                model.addAttribute(ERROR, "Category " + name + " already exists");
                return MAIN_PAGE;
            }

            Category category = new Category(name, price, amount, true);

            categoryService.save(category);

            model.addAttribute(SUCCESS, "Category " + name + " added");

        } catch (Exception e) {
            model.addAttribute("error", "Wrong Add Category command, " +
                    "use pattern: addCategory \"Chocolate bar\" 32.75 12 ");
        }
        return MAIN_PAGE;
    }


    @RequestMapping(value = "/addItem")
    public String addItem(@ModelAttribute("command") String command,
                             @ModelAttribute("lastIndex") Integer lastIndex,
                             @ModelAttribute("values") String[] values,
                             Model model) {

        try {
            String name = parseName(command);

            Category category = categoryService.findByName(name);
            Integer amount = Integer.valueOf(values[lastIndex]);

            category.setAmount(amount);

            categoryService.save(category);

            model.addAttribute(SUCCESS, amount + " items added to Category " + name);

        } catch (Exception e) {
            model.addAttribute("error", "Wrong Add Item command, " +
                    "use pattern: addItem \"Chocolate bar\" 25");
        }
        return MAIN_PAGE;
    }

    @RequestMapping(value = "/purchase")
    public String purchase(@ModelAttribute("command") String command,
                          @ModelAttribute("lastIndex") Integer lastIndex,
                          @ModelAttribute("values") String[] values,
                          Model model) {

        try {
            String name = parseName(command);
            String date = values[lastIndex];
            Date purchaseDate = dateFormat.parse(date);
            Category category = categoryService.findByName(name);

            Record record = new Record();

            record.setPurchaseDate(purchaseDate);
            record.setCategory(category);

            category.setAmount(category.getAmount() - 1);

            categoryService.save(category);
            recordService.save(record);

            model.addAttribute(SUCCESS, "Item " + name + " purchased");

        } catch (Exception e) {
            model.addAttribute(ERROR, "Wrong Purchase Item command, " +
                    "use pattern: purchase \"Chocolate bar\" 2021-04-13");
        }
        return MAIN_PAGE;
    }

    @RequestMapping(value = "/list")
    public String list(Model model) {

        List<Category> list = categoryService.showList();

        List<Category> sortedList = list.stream()
                .sorted(Comparator.comparingInt(Category::getAmount))
                .collect(Collectors.toList());

        model.addAttribute(CATEGORIES, sortedList);

        return MAIN_PAGE;
    }

    @RequestMapping(value = "/clear")
    public String clear(Model model) {

        List<Category> categories = categoryService.findByAmount(ZERO_AMOUNT);

        int count = 0;

        for (Category category : categories) {

            if (category.isVisible()) {
                category.setVisible(false);
                categoryService.save(category);
                count++;
            }
        }

        if (count > 0) {
            model.addAttribute(SUCCESS, count + " categories are updated");
        } else {
            model.addAttribute(SUCCESS, "No categories with zero amount");
        }

        return MAIN_PAGE;
    }

    @RequestMapping(value = "/report")
    public String report(@ModelAttribute("values") String[] values,
                         @ModelAttribute("lastIndex") Integer lastIndex,
                         Model model) throws ParseException {

        String date = values[lastIndex];
        DateValidator dateValidator = new DateValidatorUsingDateFormat(DATE_FORMAT);
        DateValidator shortDateValidator = new DateValidatorUsingDateFormat(SHORT_DATE_FORMAT);

        if (dateValidator.isValid(date)) {

            Date startDate = dateFormat.parse(date);
            List<Record> records = recordService.getAllBetweenDates(startDate, new Date());

            List<Record> sortedRecords = records.stream()
                    .sorted(Comparator.comparing(record -> record.getCategory().getName()))
                    .collect(Collectors.toList());

            model.addAttribute(RECORDS, sortedRecords);

        } else if (shortDateValidator.isValid(date)) {

            int firstDay = 1;
            Date shortDate = shortFormat.parse(date);

            LocalDate localDate = shortDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            YearMonth month = YearMonth.of(localDate.getYear(), localDate.getMonth());
            LocalDate start = month.atDay(firstDay);
            LocalDate end   = month.atEndOfMonth();

            Date startDate = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant());

            List<Record> recordForMonth = recordService.getAllBetweenDates(startDate, endDate);

            model.addAttribute(RECORDS, recordForMonth);

        } else {
            model.addAttribute(ERROR, "Date format error, " +
                    "use pattern: report 2021-04-21 or 2021-04");
        }
        return MAIN_PAGE;
    }

    private String parseName(String command) throws Exception {

        if (command.contains(DOUBLE_QUOTES)) {
            return command.substring(command.indexOf(DOUBLE_QUOTES) + 1, command.lastIndexOf(DOUBLE_QUOTES));

        } else if (command.contains(SPECIFIC_DOUBLE_QUOTES_OPEN)) {
            return command.substring(command.indexOf(SPECIFIC_DOUBLE_QUOTES_OPEN) + 1,
                    command.lastIndexOf(SPECIFIC_DOUBLE_QUOTES_CLOSE));
        } else {
            throw new Exception();
        }
    }

}
