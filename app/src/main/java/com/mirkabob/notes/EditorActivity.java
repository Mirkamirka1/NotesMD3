package com.mirkabob.notes;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class EditorActivity extends AppCompatActivity {
    private NoteDb db;
    private Note note;
    private EditText titleEt, bodyEt;
    private View root;
    private AppBarLayout appBar;
    private LinearLayout colors;
    private String t0 = "", b0 = "";
    private int c0;
    private boolean p0, deleted;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_editor);
        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        db = new NoteDb(this);
        long id = getIntent().getLongExtra("id", 0);
        Note n = id != 0 ? db.get(id) : null;
        note = n != null ? n : new Note();

        root = findViewById(R.id.editor_root);
        appBar = findViewById(R.id.appbar);
        colors = findViewById(R.id.colors);
        titleEt = findViewById(R.id.title);
        bodyEt = findViewById(R.id.body);
        titleEt.setText(note.title);
        bodyEt.setText(note.body);
        t0 = note.title;
        b0 = note.body;
        c0 = note.color;
        p0 = note.pinned;
        buildColors();
        applyColor();

        if (note.id == 0) {
            titleEt.requestFocus();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
                    | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

    private void applyColor() {
        int c = Palette.bg(this, note.color);
        root.setBackgroundColor(c);
        appBar.setBackgroundColor(c);
    }

    private void buildColors() {
        colors.removeAllViews();
        int sel = Palette.attr(this, com.google.android.material.R.attr.colorOnSurface);
        int out = Palette.attr(this, com.google.android.material.R.attr.colorOutline);
        for (int i = 0; i < Palette.COUNT; i++) {
            final int idx = i;
            boolean on = note.color == i;
            GradientDrawable g = new GradientDrawable();
            g.setShape(GradientDrawable.OVAL);
            g.setColor(Palette.bg(this, i));
            g.setStroke(dp(on ? 3 : 1), on ? sel : out);
            View v = new View(this);
            v.setBackground(g);
            v.setOnClickListener(x -> {
                note.color = idx;
                applyColor();
                buildColors();
            });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dp(40), dp(40));
            lp.setMargins(dp(6), dp(8), dp(6), dp(8));
            colors.addView(v, lp);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor, menu);
        MenuItem pin = menu.findItem(R.id.pin);
        Drawable d = pin.getIcon();
        if (d != null) {
            d = d.mutate();
            d.setTint(note.pinned
                    ? Palette.attr(this, androidx.appcompat.R.attr.colorPrimary)
                    : Palette.attr(this, com.google.android.material.R.attr.colorOnSurfaceVariant));
            pin.setIcon(d);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.pin) {
            note.pinned = !note.pinned;
            invalidateOptionsMenu();
            return true;
        } else if (id == R.id.delete) {
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Удалить заметку?")
                    .setNegativeButton("Отмена", null)
                    .setPositiveButton("Удалить", (dlg, w) -> {
                        if (note.id != 0) db.delete(note.id);
                        deleted = true;
                        finish();
                    }).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!deleted) persist();
    }

    private void persist() {
        String t = titleEt.getText().toString().trim();
        String b = bodyEt.getText().toString().trim();
        boolean dirty = !t.equals(t0) || !b.equals(b0) || note.color != c0 || note.pinned != p0;
        if (!dirty) return;
        if (t.isEmpty() && b.isEmpty()) {
            if (note.id != 0) db.delete(note.id);
            return;
        }
        note.title = t;
        note.body = b;
        db.save(note, true);
        t0 = t;
        b0 = b;
        c0 = note.color;
        p0 = note.pinned;
    }
}
