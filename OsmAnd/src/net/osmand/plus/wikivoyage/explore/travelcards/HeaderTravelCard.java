package net.osmand.plus.wikivoyage.explore.travelcards;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.osmand.plus.OsmandApplication;
import net.osmand.plus.R;

public class HeaderTravelCard extends BaseTravelCard {

	public static final int TYPE = 4;

	private int articleItemCount;
	private String title;


	public HeaderTravelCard(OsmandApplication app, boolean nightMode, String title) {
		super(app, nightMode);
		this.title = title;
	}

	@Override
	public void bindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder) {
		if (viewHolder instanceof HeaderTravelVH) {
			final HeaderTravelVH holder = (HeaderTravelVH) viewHolder;
			holder.title.setText(title);
			if (articleItemCount > 0) {
				holder.description.setText(String.valueOf(articleItemCount));
				holder.description.setVisibility(View.VISIBLE);
				holder.progressBar.setVisibility(View.GONE);
			} else {
				holder.progressBar.setVisibility(View.VISIBLE);
				holder.description.setVisibility(View.INVISIBLE);
			}
		}
	}

	public static class HeaderTravelVH extends RecyclerView.ViewHolder {

		final TextView title;
		final TextView description;
		final ProgressBar progressBar;

		public HeaderTravelVH(View itemView) {
			super(itemView);
			title = (TextView) itemView.findViewById(R.id.title);
			description = (TextView) itemView.findViewById(R.id.description);
			progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
		}
	}

	public void setArticleItemCount(int articleItemCount) {
		this.articleItemCount = articleItemCount;
	}

	@Override
	public int getCardType() {
		return TYPE;
	}
}

