package com.mirkabob.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private NoteDb db;
    private NoteAdapter adapter;
    private TextView empty;
    private String query = "";

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));
        db = new NoteDb(this);
        empty = findViewById(R.id.empty);

        RecyclerView rv = findViewById(R.id.list);
        rv.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        adapter = new NoteAdapter(n -> open(n.id));
        rv.setAdapter(adapter);
        findViewById(R.id.fab).setOnClickListener(v -> open(0));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView r, @NonNull RecyclerView.ViewHolder a,
                                  @NonNull RecyclerView.ViewHolder b) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int dir) {
                int pos = vh.getBindingAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return;
                Note n = adapter.get(pos);
                db.delete(n.id);
                refresh();
                Snackbar.make(findViewById(R.id.root), "Заметка удалена", Snackbar.LENGTH_LONG)
                        .setAnchorView(findViewById(R.id.fab))
                        .setAction("Отменить", v -> {
                            db.save(n, false);
                            refresh();
                        }).show();
            }
        }).attachToRecyclerView(rv);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        SearchView sv = (SearchView) menu.findItem(R.id.search).getActionView();
        sv.setQueryHint("Поиск");
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String q) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String q) {
                query = q;
                refresh();
                return true;
            }
        });
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void open(long id) {
        startActivity(new Intent(this, EditorActivity.class).putExtra("id", id));
    }

    private void refresh() {
        String q = query.trim().toLowerCase(Locale.getDefault());
        List<Note> out = new ArrayList<>();
        for (Note n : db.all()) {
            if (q.isEmpty() || n.title.toLowerCase(Locale.getDefault()).contains(q)
                    || n.body.toLowerCase(Locale.getDefault()).contains(q)) out.add(n);
        }
        adapter.set(out);
        empty.setVisibility(out.isEmpty() ? View.VISIBLE : View.GONE);
        empty.setText(q.isEmpty() ? "Пока пусто.\nНажми «Создать», чтобы добавить заметку" : "Ничего не найдено");
    }
}
