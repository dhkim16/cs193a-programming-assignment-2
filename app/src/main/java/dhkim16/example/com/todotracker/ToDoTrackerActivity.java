// Dae Hyun Kim (dhkim16@stanford.edu)
// ToDoTracker v1.00
// This app allows its user to keep track of the to-do's.
// This program allows user to set priority (high / low) on the to-do's and navigate according to
// its priority.
// The program also keeps a backup of the to-do's, so that the data is not lost.
// Note: Designed on Nexus 5, portrait mode.
package dhkim16.example.com.todotracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class ToDoTrackerActivity extends AppCompatActivity {

    private DisplayState d_state;
    private ArrayList<ToDoElem> high_priority_todos;
    private ArrayList<ToDoElem> low_priority_todos;

    private ArrayList<String> todos_str;
    private ArrayAdapter<String> todo_list_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_tracker);

        d_state = DisplayState.DISP_ALL;
        high_priority_todos = new ArrayList<ToDoElem>();
        low_priority_todos = new ArrayList<ToDoElem>();
        todos_str = new ArrayList<String >();
        todo_list_adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, todos_str);
        ListView todo_list = (ListView) findViewById(R.id.todo_list);
        todo_list.setAdapter(todo_list_adapter);
        todo_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                switch (d_state)
                {
                    case DISP_ALL:
                        if (position == 0)
                        {
                            break;
                        }
                        position--;
                        if (position < high_priority_todos.size())
                        {
                            high_priority_todos.remove(position);
                            break;
                        }
                        position -= high_priority_todos.size();
                        if (position == 0)
                        {
                            break;
                        }
                        position--;
                        low_priority_todos.remove(position);
                        break;
                    case HIGH_PRIORITY:
                        high_priority_todos.remove(position);
                        break;
                    case LOW_PRIORITY:
                        low_priority_todos.remove(position);
                }
                save_all_to_file();
                build_todo_list();
                return true;
            }
        });

        load_all_from_file();
        build_todo_list();
    }

    private void load_all_from_file()
    {
        read_from_file("high_priority_todos.txt", high_priority_todos);
        read_from_file("low_priority_todos.txt", low_priority_todos);
    }

    private void read_from_file(String file_name, ArrayList<ToDoElem> target_list)
    {
        try
        {
            Scanner file_scanner = new Scanner(openFileInput(file_name));
            while (file_scanner.hasNextLine())
            {
                String line = file_scanner.nextLine();
                ToDoElem new_elem = new ToDoElem(line);
                target_list.add(new_elem);
            }
            file_scanner.close();
        }
        catch (FileNotFoundException e)
        {
            // Nothing to read from file. Don't read anything.
            return;
        }
    }

    private void save_all_to_file()
    {
        write_to_file("high_priority_todos.txt", high_priority_todos);
        write_to_file("low_priority_todos.txt", low_priority_todos);
    }

    private void write_to_file(String file_name, ArrayList<ToDoElem> source_list)
    {
        try
        {
            PrintStream out = new PrintStream(openFileOutput(file_name, MODE_PRIVATE));
            for (ToDoElem elem : source_list) {
                out.println(elem.description);
            }
            out.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void clear_input_fields()
    {
        EditText new_todo_etxt = (EditText) findViewById(R.id.new_todo_etxt);
        CheckBox priority_cbox = (CheckBox) findViewById(R.id.priority_cbox);

        new_todo_etxt.setText("");
        priority_cbox.setChecked(false);
    }

    private void build_todo_list()
    {
        todos_str.clear();
        switch (d_state)
        {
            case DISP_ALL:
                todos_str.add("--- High Priority ---");
                for (ToDoElem todo_elem: high_priority_todos)
                {
                    todos_str.add(todo_elem.description);
                }
                todos_str.add("--- Low Priority ---");
                for (ToDoElem todo_elem: low_priority_todos)
                {
                    todos_str.add(todo_elem.description);
                }
                break;
            case HIGH_PRIORITY:
                for (ToDoElem todo_elem: high_priority_todos)
                {
                    todos_str.add(todo_elem.description);
                }
                break;
            case LOW_PRIORITY:
                for (ToDoElem todo_elem: low_priority_todos)
                {
                    todos_str.add(todo_elem.description);
                }
                break;
        }
        todo_list_adapter.notifyDataSetChanged();
    }

    public void add_todo_click(View view)
    {
        EditText new_todo_etxt = (EditText) findViewById(R.id.new_todo_etxt);
        CheckBox priority_cbox = (CheckBox) findViewById(R.id.priority_cbox);
        String new_todo_description = new_todo_etxt.getText().toString();
        if (new_todo_description.isEmpty())
        {
            Toast.makeText(this, "Please enter some description for the new to-do.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        boolean new_todo_high_priority = priority_cbox.isChecked();
        ToDoElem new_todo = new ToDoElem(new_todo_description);
        if (new_todo_high_priority)
        {
            high_priority_todos.add(new_todo);
        }
        else
        {
            low_priority_todos.add(new_todo);
        }

        save_all_to_file();
        build_todo_list();
        clear_input_fields();
    }

    public void change_priority_click(View view)
    {
        String button_string = ((Button) view).getText().toString();
        TextView curr_priority_tv = (TextView) findViewById(R.id.curr_priority_tv);
        switch (button_string)
        {
            case "All":
                d_state = DisplayState.DISP_ALL;
                curr_priority_tv.setText("- All Priorities");
                break;
            case "High":
                d_state = DisplayState.HIGH_PRIORITY;
                curr_priority_tv.setText("- High Priority");
                break;
            case "Low":
                d_state = DisplayState.LOW_PRIORITY;
                curr_priority_tv.setText("- Low Priority");
                break;
        }
        build_todo_list();
    }
}
