package com.mirkabob.notes;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.VH> {
    interface Click {
        void on(Note n);
    }

    private final List<Note> items = new ArrayList<>();
    private final Click click;

    NoteAdapter(Click click) {
        this.click = click;
    }

    void set(List<Note> list) {
        items.clear();
        items.addAll(list);
        notifyDataSetChanged();
    }

    Note get(int pos) {
        return items.get(pos);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Note n = items.get(position);
        Context c = h.card.getContext();
        h.title.setText(n.title);
        h.title.setVisibility(n.title.isEmpty() ? View.GONE : View.VISIBLE);
        h.body.setText(n.body);
        h.body.setVisibility(n.body.isEmpty() ? View.GONE : View.VISIBLE);
        h.pin.setVisibility(n.pinned ? View.VISIBLE : View.GONE);
        h.date.setText(DateUtils.formatDateTime(c, n.updated,
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_MONTH));
        h.card.setCardBackgroundColor(Palette.bg(c, n.color));
        h.card.setOnClickListener(v -> click.on(n));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final MaterialCardView card;
        final TextView title, body, date;
        final ImageView pin;

        VH(View v) {
            super(v);
            card = v.findViewById(R.id.card);
            title = v.findViewById(R.id.title);
            body = v.findViewById(R.id.body);
            date = v.findViewById(R.id.date);
            pin = v.findViewById(R.id.pin);
        }
    }
}
