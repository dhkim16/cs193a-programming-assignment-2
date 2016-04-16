package dhkim16.example.com.todotracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ToDoTrackerActivity extends AppCompatActivity {

    private ArrayList<ToDoElem> high_priority_todos;
    private ArrayList<ToDoElem> low_priority_todos;

    private ArrayList<String> todos_str;
    private ArrayAdapter<String> todo_list_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_tracker);

        todos_str = new ArrayList<String>();
        todo_list_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, todos_str);
        ListView todo_list = (ListView) findViewById(R.id.todo_list);
        todo_list.setAdapter(todo_list_adapter);
        todo_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(ToDoTrackerActivity.this, "LONG CLICKED! " + todos_str.get(position), Toast.LENGTH_SHORT).show();
                todos_str.remove(position);
                todo_list_adapter.notifyDataSetChanged();
                return false;
            }
        });
    }

    private void refresh_display()
    {


    }

    public void add_todo_click(View view)
    {
        EditText new_todo_etxt = (EditText) findViewById(R.id.new_todo_etxt);
        todos_str.add(new_todo_etxt.getText().toString());
        todo_list_adapter.notifyDataSetChanged();
        new_todo_etxt.setText("");
    }
}
