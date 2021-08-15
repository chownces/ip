import java.util.ArrayList;
import java.util.Scanner;

public class Duke {
    private final String LINE = "-----------------------------------------------------------------------\n";
    private final ArrayList<Task> tasks = new ArrayList<>();

    private void startApp() {
        // Print welcome text
        String logo = " ____        _        \n"
                + "|  _ \\ _   _| | _____ \n"
                + "| | | | | | | |/ / _ \\\n"
                + "| |_| | |_| |   <  __/\n"
                + "|____/ \\__,_|_|\\_\\___|\n";
        System.out.println(LINE + logo + "\nHello! I'm Duke :)\nWhat can I do for you?\n" + LINE);

        // Handle user input
        Scanner sc = new Scanner(System.in);
        while(true) {
            this.handleCommand(sc.nextLine());
        }
    }

    private void handleCommand(String command) {
        try {
            if (command.equals("")) {
                // If user inputs empty string, do nothing
                return;
            } else if (command.equals("bye")) {
                this.handleBye();
            } else if (command.equals("list")) {
                this.handleList();
            } else if (command.matches("^done -?[0-9]+$")) {
                this.handleDone(command);
            } else if (command.matches("^todo( .*)?")) {
                this.handleTodo(command);
            } else if (command.matches("^deadline( .*)?")) {
                this.handleDeadline(command);
            } else if (command.matches("^event( .*)?")) {
                this.handleEvent(command);
            } else {
                throw new UnknownCommandException();
            }
        } catch (DukeException e) {
            System.out.println(LINE + e.getMessage() + "\n" + LINE);
        }
    }

    private void handleBye() {
        System.out.println(LINE + "Bye. Hope to see you again soon!\n" + LINE);
        System.exit(0);
    }

    private void handleList() {
        System.out.println(LINE + "Here are the tasks in your list:");
        for (int i = 0; i < this.tasks.size(); i++) {
            System.out.printf("%d.%s%n", i + 1, this.tasks.get(i));
        }
        System.out.println(LINE);
    }

    private void handleDone(String input) throws InvalidTaskIndexException {
        String[] splitInput = input.split(" ");
        int taskIdx = -1;

        // We should not have an error here as we have performed regex string matching above
        // But just to be safe
        try {
            taskIdx = Integer.parseInt(splitInput[1]);
        } catch (NumberFormatException e) {
            System.out.println(LINE + "Invalid input!\n" + LINE);
        }

        // Handle invalid index
        if (taskIdx >= 1 && taskIdx <= tasks.size()) {
            Task t = tasks.get(taskIdx - 1);
            t.markAsDone();
            System.out.println(LINE + String.format("Nice! I've marked this task as done:\n  %s\n", t) + LINE);
        } else {
            throw new InvalidTaskIndexException();
        }
    }

    private void handleTodo(String command) throws EmptyTodoDescriptionException {
        String description = command.substring(4).trim();
        if (description.equals("")) {
            throw new EmptyTodoDescriptionException();
        }
        Todo newTodo = new Todo(description);
        tasks.add(newTodo);
        System.out.println(this.formatAddTaskString(newTodo));
    }

    private void handleDeadline(String command) throws InvalidFormatException {
        this.validateRegex(command, "^deadline .+ /by .+", "deadline {description} /by {time}");
        String[] info = command.substring(8).split("/by");
        Deadline newDeadline = new Deadline(info[0].trim(), info[1].trim());
        tasks.add(newDeadline);
        System.out.println(formatAddTaskString(newDeadline));
    }

    private void handleEvent(String command) throws InvalidFormatException {
        this.validateRegex(command, "^event .+ /at .+", "event {description} /at {time}");
        String[] info = command.substring(5).split("/at");
        Event newEvent = new Event(info[0].trim(), info[1].trim());
        tasks.add(newEvent);
        System.out.println(formatAddTaskString(newEvent));
    }

    private String formatAddTaskString(Task task) {
        return LINE +
                String.format("Got it. I've added this task:\n  %s\nNow you have %d task%s in the list.\n",
                        task, this.tasks.size(), this.tasks.size() == 1 ? "" : "s") + LINE;
    }

    private void validateRegex(String str, String regex, String validFormat) throws InvalidFormatException {
        if (!str.matches(regex)) {
            throw new InvalidFormatException(validFormat);
        }
    }

    public static void main(String[] args) {
        new Duke().startApp();
    }
}
