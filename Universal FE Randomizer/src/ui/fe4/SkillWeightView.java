package ui.fe4;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;

import ui.fe4.WeightView.WeightViewListener;
import ui.fe4.WeightedOptions.Weight;

public class SkillWeightView extends Composite {
	
	private WeightView wrathView;
	private WeightView adeptView;
	private WeightView charmView;
	private WeightView nihilView;
	private WeightView miracleView;
	private WeightView criticalView;
	private WeightView vantageView;
	private WeightView chargeView;
	private WeightView astraView;
	private WeightView lunaView;
	private WeightView solView;
	private WeightView renewalView;
	private WeightView paragonView;
	private WeightView bargainView;
	
	private Label pursuitLabel;
	private Spinner pursuitSpinner;
	
	private Set<WeightView> allViews;
	
	private SkillWeightsListener listener;
	private boolean allItemsDisabled;
	
	private class WeightListener implements WeightViewListener {
		private void notifyCount() {
			int count = 0;
			for (WeightView view : allViews) {
				if (view.optionEnabled()) { count++; }
			}
			
			if (pursuitSpinner.getSelection() > 0) { count++; }
			notifyEnableCountChanged(count);
			if (count == 0) {
				notifyAllItemsDisabled();
			}
		}
		
		@Override
		public void onWeightChanged(Weight oldWeight, Weight newWeight) {}
		
		@Override
		public void onItemEnabled() { notifyCount(); }
		
		@Override
		public void onItemDisabled() { notifyCount(); }
	}
	
	private WeightListener weightListener = new WeightListener();
	
	public interface SkillWeightsListener {
		public void onAllItemsDisabled();
		public void onEnableCountChanged(int enabledCount);
	}
	
	private void notifyAllItemsDisabled() {
		allItemsDisabled = true;
		if (listener != null) {
			listener.onAllItemsDisabled();
		}
	}
	
	private void notifyEnableCountChanged(int count) {
		if (listener != null) {
			listener.onEnableCountChanged(count);
		}
	}
	
	public void setListener(SkillWeightsListener listener) {
		this.listener = listener;
	}
	
	public SkillWeightView(Composite parent, int style) {
		super(parent, style);
		
		FormLayout formLayout = new FormLayout();
		formLayout.marginTop = 10;
		formLayout.marginLeft = 10;
		setLayout(formLayout);
		
		Label titleLabel = new Label(this, SWT.NONE);
		titleLabel.setText("Skill Weights");
		
		FormData titleData = new FormData();
		titleData.left = new FormAttachment(0, 0);
		titleLabel.setLayoutData(titleData);
		
		Composite header = new Composite(this, SWT.NONE);
		
		FormData headerData = new FormData();
		headerData.left = new FormAttachment(0, 5);
		headerData.top = new FormAttachment(titleLabel, 5);
		headerData.right = new FormAttachment(100, -5);
		header.setLayoutData(headerData);
		
		FormLayout headerLayout = new FormLayout();
		header.setLayout(headerLayout);
		
		Label allowLabel = new Label(header, SWT.NONE);
		allowLabel.setText("Allow?");
		
		FormData allowData = new FormData();
		allowData.left = new FormAttachment(0, 0);
		allowData.top = new FormAttachment(0, 0);
		allowLabel.setLayoutData(allowData);
		
		Label lessLikely = new Label(header, SWT.NONE);
		lessLikely.setText("Less Likely");
		
		FormData lessData = new FormData();
		lessData.left = new FormAttachment(0, 80);
		lessData.top = new FormAttachment(allowLabel, 0, SWT.CENTER);
		lessLikely.setLayoutData(lessData);
		
		Label moreLikely = new Label(header, SWT.NONE);
		moreLikely.setText("More Likely");
		
		FormData moreData = new FormData();
		moreData.right = new FormAttachment(100, -5);
		moreData.top = new FormAttachment(allowLabel, 0, SWT.CENTER);
		moreLikely.setLayoutData(moreData);
		
		wrathView = new WeightView("Wrath", Weight.NORMAL, this, SWT.NONE);
		wrathView.setListener(weightListener);
		
		FormData viewData = new FormData();
		viewData.left = new FormAttachment(header, 0, SWT.LEFT);
		viewData.top = new FormAttachment(header, 5);
		viewData.right = new FormAttachment(100, -5);
		wrathView.setLayoutData(viewData);
		
		adeptView = new WeightView("Adept", Weight.NORMAL, this, SWT.NONE);
		adeptView.setListener(weightListener);
		
		viewData = new FormData();
		viewData.left = new FormAttachment(header, 0, SWT.LEFT);
		viewData.top = new FormAttachment(wrathView, 0);
		viewData.right = new FormAttachment(100, -5);
		adeptView.setLayoutData(viewData);
		
		charmView = new WeightView("Charm", Weight.NORMAL, this, SWT.NONE);
		charmView.setListener(weightListener);
		
		viewData = new FormData();
		viewData.left = new FormAttachment(header, 0, SWT.LEFT);
		viewData.top = new FormAttachment(adeptView, 0);
		viewData.right = new FormAttachment(100, -5);
		charmView.setLayoutData(viewData);
		
		nihilView = new WeightView("Nihil", Weight.NORMAL, this, SWT.NONE);
		nihilView.setListener(weightListener);
		
		viewData = new FormData();
		viewData.left = new FormAttachment(header, 0, SWT.LEFT);
		viewData.top = new FormAttachment(charmView, 0);
		viewData.right = new FormAttachment(100, -5);
		nihilView.setLayoutData(viewData);
		
		miracleView = new WeightView("Miracle", Weight.NORMAL, this, SWT.NONE);
		miracleView.setListener(weightListener);
		
		viewData = new FormData();
		viewData.left = new FormAttachment(header, 0, SWT.LEFT);
		viewData.top = new FormAttachment(nihilView, 0);
		viewData.right = new FormAttachment(100, -5);
		miracleView.setLayoutData(viewData);
		
		criticalView = new WeightView("Critical", Weight.NORMAL, this, SWT.NONE);
		criticalView.setListener(weightListener);
		
		viewData = new FormData();
		viewData.left = new FormAttachment(header, 0, SWT.LEFT);
		viewData.top = new FormAttachment(miracleView, 0);
		viewData.right = new FormAttachment(100, -5);
		criticalView.setLayoutData(viewData);
		
		vantageView = new WeightView("Vantage", Weight.NORMAL, this, SWT.NONE);
		vantageView.setListener(weightListener);
		
		viewData = new FormData();
		viewData.left = new FormAttachment(header, 0, SWT.LEFT);
		viewData.top = new FormAttachment(criticalView, 0);
		viewData.right = new FormAttachment(100, -5);
		vantageView.setLayoutData(viewData);
		
		chargeView = new WeightView("Charge", Weight.NORMAL, this, SWT.NONE);
		chargeView.setListener(weightListener);
		
		viewData = new FormData();
		viewData.left = new FormAttachment(header, 0, SWT.LEFT);
		viewData.top = new FormAttachment(vantageView, 0);
		viewData.right = new FormAttachment(100, -5);
		chargeView.setLayoutData(viewData);
		
		astraView = new WeightView("Astra", Weight.LOW, this, SWT.NONE);
		astraView.setListener(weightListener);
		
		viewData = new FormData();
		viewData.left = new FormAttachment(header, 0, SWT.LEFT);
		viewData.top = new FormAttachment(chargeView, 0);
		viewData.right = new FormAttachment(100, -5);
		astraView.setLayoutData(viewData);
		
		lunaView = new WeightView("Luna", Weight.LOW, this, SWT.NONE);
		lunaView.setListener(weightListener);
		
		viewData = new FormData();
		viewData.left = new FormAttachment(header, 0, SWT.LEFT);
		viewData.top = new FormAttachment(astraView, 0);
		viewData.right = new FormAttachment(100, -5);
		lunaView.setLayoutData(viewData);
		
		solView = new WeightView("Sol", Weight.LOW, this, SWT.NONE);
		solView.setListener(weightListener);
		
		viewData = new FormData();
		viewData.left = new FormAttachment(header, 0, SWT.LEFT);
		viewData.top = new FormAttachment(lunaView, 0);
		viewData.right = new FormAttachment(100, -5);
		solView.setLayoutData(viewData);
		
		renewalView = new WeightView("Renewal", Weight.NORMAL, this, SWT.NONE);
		renewalView.setListener(weightListener);
		
		viewData = new FormData();
		viewData.left = new FormAttachment(header, 0, SWT.LEFT);
		viewData.top = new FormAttachment(solView, 0);
		viewData.right = new FormAttachment(100, -5);
		renewalView.setLayoutData(viewData);
		
		paragonView = new WeightView("Paragon", Weight.NORMAL, this, SWT.NONE);
		paragonView.setListener(weightListener);
		
		viewData = new FormData();
		viewData.left = new FormAttachment(header, 0, SWT.LEFT);
		viewData.top = new FormAttachment(renewalView, 0);
		viewData.right = new FormAttachment(100, -5);
		paragonView.setLayoutData(viewData);
		
		bargainView = new WeightView("Bargain", Weight.LOW, this, SWT.NONE);
		bargainView.setListener(weightListener);
		
		viewData = new FormData();
		viewData.left = new FormAttachment(header, 0, SWT.LEFT);
		viewData.top = new FormAttachment(paragonView, 0);
		viewData.right = new FormAttachment(100, -5);
		bargainView.setLayoutData(viewData);
		
		pursuitSpinner = new Spinner(this, SWT.NONE);
		pursuitSpinner.setValues(50, 0, 100, 0, 5, 10);
		pursuitSpinner.setEnabled(false);
		pursuitSpinner.setToolTipText("Due to Pursuit's outsized importance in FE4, pursuit's chance can be set independently of the skill pool specified above.\nWhether a unit has pursuit is rolled first before determining other skills.\nA unit randomized with 0 skills will not be rolled.");
		pursuitSpinner.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				weightListener.notifyCount();
			}
		});
				
		viewData = new FormData();
		viewData.right = new FormAttachment(100, -5);
		viewData.top = new FormAttachment(bargainView, 10);
		pursuitSpinner.setLayoutData(viewData);
		
		pursuitLabel = new Label(this, SWT.NONE);
		pursuitLabel.setText("Pursuit Chance:");
		pursuitLabel.setEnabled(false);
		
		FormData labelData = new FormData();
		labelData.left = new FormAttachment(header, 0, SWT.LEFT);
		labelData.top = new FormAttachment(pursuitSpinner, 0, SWT.CENTER);
		pursuitLabel.setLayoutData(labelData);
		
		allViews = new HashSet<WeightView>(Arrays.asList(wrathView, adeptView, charmView, nihilView, miracleView, criticalView, 
				vantageView, chargeView, astraView, lunaView, solView, renewalView, paragonView, bargainView));
	}
	
	public void setEnabled(boolean enabled) {
		for (WeightView view : allViews) { view.setEnabled(enabled); }
		pursuitSpinner.setEnabled(enabled);
		pursuitLabel.setEnabled(enabled);
		
		if (allItemsDisabled && enabled) {
			for (WeightView view : allViews) { view.setSelected(true); }
			allItemsDisabled = false;
		}
	}
	
	
	public SkillWeightOptions getSkillWeights() {
		return new SkillWeightOptions(wrathView.getWeightedOptions(),  
				adeptView.getWeightedOptions(), 
				charmView.getWeightedOptions(), 
				nihilView.getWeightedOptions(), 
				miracleView.getWeightedOptions(), 
				criticalView.getWeightedOptions(), 
				vantageView.getWeightedOptions(), 
				chargeView.getWeightedOptions(), 
				astraView.getWeightedOptions(), 
				lunaView.getWeightedOptions(), 
				solView.getWeightedOptions(), 
				renewalView.getWeightedOptions(), 
				paragonView.getWeightedOptions(), 
				bargainView.getWeightedOptions(),
				pursuitSpinner.getSelection());
	}

	public void setSkillWeights(SkillWeightOptions options) {
		if (options == null) {
			// Shouldn't happen.
		} else {
			wrathView.setWeightedOptions(options.wrathWeight);
			adeptView.setWeightedOptions(options.adeptWeight);
			charmView.setWeightedOptions(options.charmWeight);
			nihilView.setWeightedOptions(options.nihilWeight);
			miracleView.setWeightedOptions(options.miracleWeight);
			criticalView.setWeightedOptions(options.criticalWeight);
			vantageView.setWeightedOptions(options.vantageWeight);
			chargeView.setWeightedOptions(options.chargeWeight);
			astraView.setWeightedOptions(options.astraWeight);
			lunaView.setWeightedOptions(options.lunaWeight);
			solView.setWeightedOptions(options.solWeight);
			renewalView.setWeightedOptions(options.renewalWeight);
			paragonView.setWeightedOptions(options.paragonWeight);
			bargainView.setWeightedOptions(options.bargainWeight);
			
			pursuitSpinner.setSelection(options.pursuitChance);
		}
	}
}
