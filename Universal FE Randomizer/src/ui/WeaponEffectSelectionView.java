package ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import fedata.general.FEBase.GameType;
import ui.general.MinMaxControl;
import ui.model.WeaponEffectOptions;

public class WeaponEffectSelectionView extends Composite {
	
	public interface WeaponEffectSelectionViewListener {
		public void onSelectionChanged();
	}
	
	private Button statBoostsCheckBox;
	private Button effectivenessCheckBox;
	private Button unbreakableCheckBox;
	private Button braveCheckBox;
	private Button reverseTriangleCheckBox;
	private Button extendedRangeCheckBox;
	private Button highCriticalCheckBox;
	private MinMaxControl criticalControl;
	private Button magicDamageCheckBox;
	private Button poisonCheckBox;
	private Button stealHPCheckBox;
	private Button critImmuneCheckBox;
	private Button noCritCheckBox;
	private Button eclipseCheckBox;
	private Button devilCheckBox;
	
	public Boolean noneEnabled;
	public Boolean statBoostsEnabled;
	public Boolean effectivenessEnabled;
	public Boolean unbreakableEnabled;
	public Boolean braveEnabled;
	public Boolean reverseEnabled;
	public Boolean rangeEnabled;
	public Boolean criticalEnabled;
	public Boolean magicEnabled;
	public Boolean poisonEnabled;
	public Boolean stealHPEnabled;
	public Boolean critImmuneEnabled;
	public Boolean noCritEnabled;
	public Boolean eclipseEnabled;
	public Boolean devilEnabled;
	
	private WeaponEffectSelectionViewListener listener;
	private boolean squelchCallbacks;

	public WeaponEffectSelectionView(Composite parent, int style, GameType type) {
		super(parent, style);
		
		RowLayout rowLayout = new RowLayout();
		rowLayout.pack = false;
		setLayout(rowLayout);
		
		buildView(type);
	}
	
	protected void buildView(GameType type) {
		statBoostsCheckBox = new Button(this, SWT.CHECK);
		statBoostsCheckBox.setText("Stat Boosts");
		statBoostsCheckBox.setToolTipText("Allows random weapons to grant minor stat boosts. (+5 to a random stat).");
		statBoostsCheckBox.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				notifySelectionChange();
			}
		});
		
		effectivenessCheckBox = new Button(this, SWT.CHECK);
		effectivenessCheckBox.setText("Effectiveness");
		effectivenessCheckBox.setToolTipText("Allows random weapons to be effective against certain unit types.");
		effectivenessCheckBox.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				notifySelectionChange();
			}
		});
		
		unbreakableCheckBox = new Button(this, SWT.CHECK);
		unbreakableCheckBox.setText("Unbreakable");
		unbreakableCheckBox.setToolTipText("Allows random weapons to have infinite durability.");
		unbreakableCheckBox.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				notifySelectionChange();
			}
		});
		
		braveCheckBox = new Button(this, SWT.CHECK);
		braveCheckBox.setText("Brave");
		braveCheckBox.setToolTipText("Allows random weapons to strike twice per attack.");
		braveCheckBox.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				notifySelectionChange();
			}
		});
		
		reverseTriangleCheckBox = new Button(this, SWT.CHECK);
		if (type == GameType.FE9) {
			reverseTriangleCheckBox.setText("Shifted Triangle");
			reverseTriangleCheckBox.setToolTipText("Allows random weapons to have a different triangle type from their equip type\n(e.g. Axes that act like Swords (good against other axes, weak against lances).)");
		} else {
			reverseTriangleCheckBox.setText("Reverse Triangle");
			reverseTriangleCheckBox.setToolTipText("Allows random weapons to reverse their Weapon Triangle advantage and disadvantage.");
		}
		reverseTriangleCheckBox.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				notifySelectionChange();
			}
		});
		
		extendedRangeCheckBox = new Button(this, SWT.CHECK);
		extendedRangeCheckBox.setText("Extended Range");
		extendedRangeCheckBox.setToolTipText("Allows random weapons to gain range. In most cases, max range is increased, though bows may gain the ability to be melee weapons. Siege tomes are unaffected.");
		extendedRangeCheckBox.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				notifySelectionChange();
			}
		});
		
		highCriticalCheckBox = new Button(this, SWT.CHECK);
		highCriticalCheckBox.setText("Critical");
		highCriticalCheckBox.setToolTipText("Allows random weapons to gain a large critical bonus.");
		highCriticalCheckBox.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				notifySelectionChange();
				criticalControl.setEnabled(highCriticalCheckBox.getSelection());
			}
		});
		
		criticalControl = new MinMaxControl(this, SWT.NONE, "", "~");
		criticalControl.getMinSpinner().setValues(20, 5, 50, 0, 5, 5);
		criticalControl.getMaxSpinner().setValues(50, 20, 100, 0, 5, 5);
		criticalControl.setEnabled(false);
		
		magicDamageCheckBox = new Button(this, SWT.CHECK);
		magicDamageCheckBox.setText("Magic Damage");
		if (type == GameType.FE9) {
			magicDamageCheckBox.setToolTipText("Allows random physical weapons to gain a magic attack that targets RES. Tomes are unaffected.");
		} else {
			magicDamageCheckBox.setToolTipText("Allows random physical weapons to gain a magic attack. Melee weapons gain range if they were not already ranged. Weapons get assigned a random magic animation. Tomes are unaffected.");
		}
		magicDamageCheckBox.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				notifySelectionChange();
			}
		});
		
		poisonCheckBox = new Button(this, SWT.CHECK);
		poisonCheckBox.setText("Poison");
		poisonCheckBox.setToolTipText("Allows random weapons to apply poison on hit.");
		poisonCheckBox.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				notifySelectionChange();
			}
		});
		
		if (type == GameType.FE9) {
			stealHPCheckBox = new Button(this, SWT.CHECK);
			stealHPCheckBox.setText("Steal HP");
			stealHPCheckBox.setToolTipText("Allows random weapons to drain target's HP on hit.");
			stealHPCheckBox.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					notifySelectionChange();
				}
			});
			
			critImmuneCheckBox = new Button(this, SWT.CHECK);
			critImmuneCheckBox.setText("Crit. Immunity");
			critImmuneCheckBox.setToolTipText("Allows random weapons to block non-Wrath critical hits while equipped.");
			critImmuneCheckBox.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					notifySelectionChange();
				}
			});
			
			noCritCheckBox = new Button(this, SWT.CHECK);
			noCritCheckBox.setText("Disable Crit.");
			noCritCheckBox.setToolTipText("Disables the ability to trigger critical hits when random weapons are used. Hit is automatically improved by 50 and weapon experience gained per use is increased.");
			noCritCheckBox.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					notifySelectionChange();
				}
			});
		}
		
		if (type != GameType.FE6 && type != GameType.FE9) {
			eclipseCheckBox = new Button(this, SWT.CHECK);
			eclipseCheckBox.setText("Eclipse");
			eclipseCheckBox.setToolTipText("Allows random weapons to always do half of the target's current HP.");
			eclipseCheckBox.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					notifySelectionChange();
				}
			});
		}
		
		if (type != GameType.FE9) {
			devilCheckBox = new Button(this, SWT.CHECK);
			devilCheckBox.setText("Devil");
			if (type == GameType.FE6) {
				devilCheckBox.setToolTipText("Allows random weapons to occasionally deal damage to its user instead of its target. Might is automatically improved by at least 5.");
			} else {
				devilCheckBox.setToolTipText("Allows random weapons to occasionally deal damage to its user instead of its target. Might is automatically improved by at least 5 and weapon experience gained per use is increased.");
			}
			devilCheckBox.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					notifySelectionChange();
				}
			});
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		//noneCheckBox.setEnabled(enabled);
		statBoostsCheckBox.setEnabled(enabled);
		effectivenessCheckBox.setEnabled(enabled);
		unbreakableCheckBox.setEnabled(enabled);
		braveCheckBox.setEnabled(enabled);
		reverseTriangleCheckBox.setEnabled(enabled);
		extendedRangeCheckBox.setEnabled(enabled);
		highCriticalCheckBox.setEnabled(enabled);
		magicDamageCheckBox.setEnabled(enabled);
		poisonCheckBox.setEnabled(enabled);
		if (stealHPCheckBox != null) { stealHPCheckBox.setEnabled(enabled); }
		if (critImmuneCheckBox != null) { critImmuneCheckBox.setEnabled(enabled); }
		if (noCritCheckBox != null) { noCritCheckBox.setEnabled(enabled); }
		if (eclipseCheckBox != null) { eclipseCheckBox.setEnabled(enabled); }
		if (devilCheckBox != null) { devilCheckBox.setEnabled(enabled); }
	}
	
	public void setSelectionListener(WeaponEffectSelectionViewListener weaponEffectSelectionViewListener) {
		this.listener = weaponEffectSelectionViewListener;
	}
	
	public void selectAll() {
		squelchCallbacks = true;
		//noneCheckBox.setSelection(true);
		statBoostsCheckBox.setSelection(true);
		effectivenessCheckBox.setSelection(true);
		unbreakableCheckBox.setSelection(true);
		braveCheckBox.setSelection(true);
		reverseTriangleCheckBox.setSelection(true);
		extendedRangeCheckBox.setSelection(true);
		highCriticalCheckBox.setSelection(true);
		criticalControl.setEnabled(true);
		magicDamageCheckBox.setSelection(true);
		poisonCheckBox.setSelection(true);
		if (stealHPCheckBox != null) { stealHPCheckBox.setSelection(true); }
		if (critImmuneCheckBox != null) { critImmuneCheckBox.setSelection(true); }
		if (noCritCheckBox != null) { noCritCheckBox.setSelection(true); }
		if (eclipseCheckBox != null) { eclipseCheckBox.setSelection(true); }
		if (devilCheckBox != null) { devilCheckBox.setSelection(true); }
		squelchCallbacks = false;
		
		notifySelectionChange();
	}
	
	public void deselectAll() {
		squelchCallbacks = true;
		//noneCheckBox.setSelection(false);
		statBoostsCheckBox.setSelection(false);
		effectivenessCheckBox.setSelection(false);
		unbreakableCheckBox.setSelection(false);
		braveCheckBox.setSelection(false);
		reverseTriangleCheckBox.setSelection(false);
		extendedRangeCheckBox.setSelection(false);
		highCriticalCheckBox.setSelection(false);
		criticalControl.setEnabled(false);
		magicDamageCheckBox.setSelection(false);
		poisonCheckBox.setSelection(false);
		if (stealHPCheckBox != null) { stealHPCheckBox.setSelection(false); }
		if (critImmuneCheckBox != null) { critImmuneCheckBox.setSelection(false); }
		if (noCritCheckBox != null) { noCritCheckBox.setSelection(false); }
		if (eclipseCheckBox != null) { eclipseCheckBox.setSelection(false); }
		if (devilCheckBox != null) { devilCheckBox.setSelection(false); }
		squelchCallbacks = false;
		
		notifySelectionChange();
	}
	
	public Boolean isAllDisabled() {
		return !statBoostsEnabled && !effectivenessEnabled && !unbreakableEnabled && !braveEnabled && !reverseEnabled && !rangeEnabled && !criticalEnabled && !magicEnabled && !poisonEnabled  && !stealHPEnabled && !critImmuneEnabled && !noCritEnabled && !eclipseEnabled && !devilEnabled;
	}
	
	public WeaponEffectOptions getOptions() {
		return new WeaponEffectOptions(statBoostsEnabled, effectivenessEnabled, unbreakableEnabled, braveEnabled, reverseEnabled, rangeEnabled, criticalEnabled, criticalControl.getMinMaxOption(), magicEnabled, poisonEnabled, stealHPEnabled, critImmuneEnabled, noCritEnabled, eclipseEnabled, devilEnabled);
	}
	
	public void setOptions(WeaponEffectOptions options) {
		if (options != null) {
			//noneCheckBox.setSelection(options.none != null ? options.none : false);
			statBoostsCheckBox.setSelection(options.statBoosts != null ? options.statBoosts : false);
			effectivenessCheckBox.setSelection(options.effectiveness != null ? options.effectiveness : false);
			unbreakableCheckBox.setSelection(options.unbreakable != null ? options.unbreakable : false);
			braveCheckBox.setSelection(options.brave != null ? options.brave : false);
			reverseTriangleCheckBox.setSelection(options.reverseTriangle != null ? options.reverseTriangle : false);
			extendedRangeCheckBox.setSelection(options.extendedRange != null ? options.extendedRange : false);
			highCriticalCheckBox.setSelection(options.highCritical != null ? options.highCritical : false);
			if (options.highCritical != null) {
				criticalControl.setMin(options.criticalRange.minValue);
				criticalControl.setMax(options.criticalRange.maxValue);
				criticalControl.setEnabled(options.highCritical);
			}
			magicDamageCheckBox.setSelection(options.magicDamage != null ? options.magicDamage : false);
			poisonCheckBox.setSelection(options.poison != null ? options.poison : false);
			if (stealHPCheckBox != null) { stealHPCheckBox.setSelection(options.stealHP != null ? options.stealHP : false); }
			if (critImmuneCheckBox != null) { critImmuneCheckBox.setSelection(options.critImmune != null ? options.critImmune : false); }
			if (noCritCheckBox != null) { noCritCheckBox.setSelection(options.noCrit != null ? options.noCrit : false); }
			if (eclipseCheckBox != null) { eclipseCheckBox.setSelection(options.eclipse != null ? options.eclipse : false); }
			if (devilCheckBox != null) { devilCheckBox.setSelection(options.devil != null ? options.devil : false); }
			
			notifySelectionChange();
		}
	}
	
	public void notifySelectionChange() {
		//noneEnabled = noneCheckBox.getSelection();
		statBoostsEnabled = statBoostsCheckBox.getSelection();
		effectivenessEnabled = effectivenessCheckBox.getSelection();
		unbreakableEnabled = unbreakableCheckBox.getSelection();
		braveEnabled = braveCheckBox.getSelection();
		reverseEnabled = reverseTriangleCheckBox.getSelection();
		rangeEnabled = extendedRangeCheckBox.getSelection();
		criticalEnabled = highCriticalCheckBox.getSelection();
		magicEnabled = magicDamageCheckBox.getSelection();
		poisonEnabled = poisonCheckBox.getSelection();
		stealHPEnabled = stealHPCheckBox != null ? stealHPCheckBox.getSelection() : false;
		critImmuneEnabled = critImmuneCheckBox != null ? critImmuneCheckBox.getSelection() : false;
		noCritEnabled = noCritCheckBox != null ? noCritCheckBox.getSelection() : false;
		eclipseEnabled = eclipseCheckBox != null ? eclipseCheckBox.getSelection() : false;
		devilEnabled = devilCheckBox != null ? devilCheckBox.getSelection() : false;
		
		if (!squelchCallbacks && listener != null) {
			listener.onSelectionChanged();
		}
	}
}
