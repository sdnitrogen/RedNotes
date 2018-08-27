package notes.rednitrogen.com.rednotes;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import notes.rednitrogen.com.rednotes.R;
import notes.rednitrogen.com.rednotes.database.model.Note;


public class TrashAdapter extends RecyclerView.Adapter<TrashAdapter.MyViewHolder> {

    private Context context;
    private List<Note> deletedNotesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView note;
        public TextView title;
        public TextView dot;
        public TextView timestamp;

        public MyViewHolder(View view) {
            super(view);
            note = view.findViewById(R.id.trash_note);
            title = view.findViewById(R.id.trash_note_title);
            dot = view.findViewById(R.id.trash_dot);
            timestamp = view.findViewById(R.id.trash_timestamp);
        }
    }


    public TrashAdapter(Context context, List<Note> deletedNotesList) {
        this.context = context;
        this.deletedNotesList = deletedNotesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trash_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Note note = deletedNotesList.get(position);

        holder.note.setText(note.getNote());
        holder.title.setText(note.getTitle());

        // Displaying dot from HTML character code
        holder.dot.setText(Html.fromHtml("&#8226;"));

        // Formatting and displaying timestamp
        holder.timestamp.setText(formatDate(note.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return deletedNotesList.size();
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = fmt.parse(dateStr);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }

        return "";
    }

    public void setNotesList(List<Note> deletedNotesList){
        this.deletedNotesList = deletedNotesList;
        notifyDataSetChanged();
    }
}
