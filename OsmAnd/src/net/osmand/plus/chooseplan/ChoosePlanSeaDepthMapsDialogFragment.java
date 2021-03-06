package net.osmand.plus.chooseplan;

import android.app.Activity;
import android.view.View;

import net.osmand.plus.R;
import net.osmand.plus.activities.OsmandInAppPurchaseActivity;
import net.osmand.plus.inapp.InAppPurchaseHelper;

public class ChoosePlanSeaDepthMapsDialogFragment extends ChoosePlanDialogFragment {
	public static final String TAG = ChoosePlanSeaDepthMapsDialogFragment.class.getSimpleName();

	private final OsmAndFeature[] osmLiveFeatures = {
			OsmAndFeature.CONTOUR_LINES_HILLSHADE_MAPS,
			OsmAndFeature.SEA_DEPTH_MAPS,
			OsmAndFeature.WIKIPEDIA_OFFLINE,
			OsmAndFeature.WIKIVOYAGE_OFFLINE,
			OsmAndFeature.DAILY_MAP_UPDATES,
			OsmAndFeature.UNLIMITED_DOWNLOADS,
			OsmAndFeature.UNLOCK_ALL_FEATURES,
			OsmAndFeature.DONATION_TO_OSM,
	};
	private final OsmAndFeature[] selectedOsmLiveFeatures = {
			OsmAndFeature.CONTOUR_LINES_HILLSHADE_MAPS,
			OsmAndFeature.SEA_DEPTH_MAPS,
	};

	private final OsmAndFeature[] planTypeFeatures = {
			OsmAndFeature.SEA_DEPTH_MAPS,
	};
	private final OsmAndFeature[] selectedPlanTypeFeatures = {};

	@Override
	public OsmAndFeature[] getOsmLiveFeatures() {
		return osmLiveFeatures;
	}

	@Override
	public OsmAndFeature[] getPlanTypeFeatures() {
		return planTypeFeatures;
	}

	@Override
	public OsmAndFeature[] getSelectedOsmLiveFeatures() {
		return selectedOsmLiveFeatures;
	}

	@Override
	public OsmAndFeature[] getSelectedPlanTypeFeatures() {
		return selectedPlanTypeFeatures;
	}

	@Override
	public String getInfoDescription() {
		return "";
	}

	@Override
	public int getPlanTypeHeaderImageId() {
		return R.drawable.img_logo_38dp_sea_depth;
	}

	@Override
	public String getPlanTypeHeaderTitle() {
		return getString(R.string.index_item_depth_contours_osmand_ext);
	}

	@Override
	public String getPlanTypeHeaderDescription() {
		return getString(R.string.in_app_purchase);
	}

	@Override
	public String getPlanTypeButtonTitle() {
		InAppPurchaseHelper purchaseHelper = getOsmandApplication().getInAppPurchaseHelper();
		if (purchaseHelper == null || !purchaseHelper.hasPrices()) {
			return getString(R.string.purchase_unlim_title, getString(R.string.sea_depth_maps_price));
		} else {
			return getString(R.string.purchase_unlim_title, purchaseHelper.getDepthContoursPrice());
		}
	}

	@Override
	public String getPlanTypeButtonDescription() {
		return getString(R.string.in_app_purchase_desc);
	}

	@Override
	public void setPlanTypeButtonClickListener(View button) {
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Activity activity = getActivity();
				if (activity != null) {
					OsmandInAppPurchaseActivity.purchaseDepthContours(activity);
				}
			}
		});
	}
}
