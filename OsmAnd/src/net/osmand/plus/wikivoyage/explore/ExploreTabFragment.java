package net.osmand.plus.wikivoyage.explore;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.osmand.plus.OsmandApplication;
import net.osmand.plus.R;
import net.osmand.plus.Version;
import net.osmand.plus.activities.LocalIndexHelper;
import net.osmand.plus.activities.LocalIndexInfo;
import net.osmand.plus.activities.OsmandActionBarActivity;
import net.osmand.plus.base.BaseOsmAndFragment;
import net.osmand.plus.download.DownloadResources;
import net.osmand.plus.download.ui.AbstractLoadLocalIndexTask;
import net.osmand.plus.wikivoyage.data.TravelArticle;
import net.osmand.plus.wikivoyage.data.TravelDbHelper;
import net.osmand.plus.wikivoyage.explore.travelcards.ArticleTravelCard;
import net.osmand.plus.wikivoyage.explore.travelcards.BaseTravelCard;
import net.osmand.plus.wikivoyage.explore.travelcards.HeaderTravelCard;
import net.osmand.plus.wikivoyage.explore.travelcards.OpenBetaTravelCard;
import net.osmand.plus.wikivoyage.explore.travelcards.StartEditingTravelCard;
import net.osmand.plus.wikivoyage.explore.travelcards.TravelDownloadUpdateCard;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ExploreTabFragment extends BaseOsmAndFragment {

	private static final int DOWNLOAD_UPDATE_CARD_POSITION = 0;

	private ExploreRvAdapter adapter = new ExploreRvAdapter();
	private StartEditingTravelCard startEditingTravelCard;

	private boolean nightMode;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		nightMode = !getMyApplication().getSettings().isLightContent();

		final View mainView = inflater.inflate(R.layout.fragment_explore_tab, container, false);
		final RecyclerView rv = (RecyclerView) mainView.findViewById(R.id.recycler_view);

		adapter.setItems(generateItems());

		rv.setLayoutManager(new LinearLayoutManager(getContext()));
		rv.setAdapter(adapter);

		return mainView;
	}

	private List<BaseTravelCard> generateItems() {
		final List<BaseTravelCard> items = new ArrayList<>();
		final OsmandApplication app = getMyApplication();

		addDownloadUpdateCard();
		startEditingTravelCard = new StartEditingTravelCard(app, nightMode);
		addOpenBetaTravelCard(items, nightMode);
		items.add(startEditingTravelCard);
		items.add(new HeaderTravelCard(app, nightMode, getString(R.string.popular_destinations)));
		addPopularDestinations(app);

		return items;
	}

	private void addDownloadUpdateCard() {
		final OsmandApplication app = getMyApplication();
		new CheckWorldWikivoyageTask(app, new CheckWorldWikivoyageTask.Callback() {
			@Override
			public void onCheckFinished(boolean worldWikivoyageDownloaded) {
				if (!worldWikivoyageDownloaded && adapter != null) {
					TravelDownloadUpdateCard card = new TravelDownloadUpdateCard(app, nightMode, true);
					if (adapter.addItem(DOWNLOAD_UPDATE_CARD_POSITION, card)) {
						adapter.notifyDataSetChanged();
					}
				}
			}
		}).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	private void addPopularDestinations(OsmandApplication app) {
		PopularDestinationsSearchTask popularDestinationsSearchTask = new PopularDestinationsSearchTask(
				app.getTravelDbHelper(), getMyActivity(), adapter, nightMode, startEditingTravelCard);
		popularDestinationsSearchTask.execute();
	}

	private void addOpenBetaTravelCard(List<BaseTravelCard> items, final boolean nightMode) {
		final OsmandApplication app = getMyApplication();
		if ((Version.isFreeVersion(app) && !app.getSettings().LIVE_UPDATES_PURCHASED.get()
				&& !app.getSettings().FULL_VERSION_PURCHASED.get())) {
			items.add(new OpenBetaTravelCard(app, nightMode, getFragmentManager()));
		}
	}

	private static class CheckWorldWikivoyageTask extends AsyncTask<Void, Void, Boolean> {

		private OsmandApplication app;
		private Callback callback;

		CheckWorldWikivoyageTask(OsmandApplication app, Callback callback) {
			this.app = app;
			this.callback = callback;
		}

		@Override
		protected Boolean doInBackground(Void... voids) {
			final boolean[] worldWikivoyageDownloaded = new boolean[1];
			new LocalIndexHelper(app).getLocalTravelFiles(new AbstractLoadLocalIndexTask() {
				@Override
				public void loadFile(LocalIndexInfo... loaded) {
					for (LocalIndexInfo lii : loaded) {
						if (lii.getBaseName().toLowerCase().equals(DownloadResources.WORLD_WIKIVOYAGE_NAME)) {
							worldWikivoyageDownloaded[0] = true;
						}
					}
				}
			});
			return worldWikivoyageDownloaded[0];
		}

		@Override
		protected void onPostExecute(Boolean worldWikivoyageDownloaded) {
			if (callback != null) {
				callback.onCheckFinished(worldWikivoyageDownloaded);
			}
			callback = null;
		}

		interface Callback {
			void onCheckFinished(boolean worldWikivoyageDownloaded);
		}
	}

	private static class PopularDestinationsSearchTask extends AsyncTask<Void, TravelDbHelper, List<TravelArticle>> {

		private TravelDbHelper travelDbHelper;
		private WeakReference<OsmandActionBarActivity> weakContext;
		private WeakReference<ExploreRvAdapter> weakAdapter;
		private WeakReference<StartEditingTravelCard> weakStartEditingTravelCard;
		private boolean nightMode;

		PopularDestinationsSearchTask(TravelDbHelper travelDbHelper,
		                              OsmandActionBarActivity context, ExploreRvAdapter adapter, boolean nightMode, StartEditingTravelCard startEditingTravelCard) {
			this.travelDbHelper = travelDbHelper;
			weakContext = new WeakReference<>(context);
			weakAdapter = new WeakReference<>(adapter);
			weakStartEditingTravelCard = new WeakReference<>(startEditingTravelCard);
			this.nightMode = nightMode;
		}

		@Override
		protected List<TravelArticle> doInBackground(Void... voids) {
			return travelDbHelper.searchPopular();
		}

		@Override
		protected void onPostExecute(List<TravelArticle> items) {
			OsmandActionBarActivity activity = weakContext.get();
			ExploreRvAdapter adapter = weakAdapter.get();
			StartEditingTravelCard startEditingTravelCard = weakStartEditingTravelCard.get();

			if (activity != null && adapter != null && startEditingTravelCard != null) {
				List<BaseTravelCard> adapterItems = adapter.getItems();

				if (adapterItems.contains(startEditingTravelCard)) {
					adapterItems.remove(startEditingTravelCard);
				}
				for (TravelArticle article : items) {
					adapterItems.add(new ArticleTravelCard(activity.getMyApplication(), nightMode, article, activity.getSupportFragmentManager()));
				}

				adapterItems.add(startEditingTravelCard);
				adapter.notifyDataSetChanged();
			}
		}
	}
}