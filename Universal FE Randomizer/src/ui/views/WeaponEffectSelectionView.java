package ui.views;

import fedata.general.FEBase.GameType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import ui.general.MinMaxControl;
import ui.views.NumericWeightView.NumericWeightViewListener;
import ui.model.WeaponEffectOptions;

import java.util.ArrayList;
import java.util.List;

public class WeaponEffectSelectionView extends Composite {
	
	public interface WeaponEffectSelectionViewListener {
		public void onSelectionChanged();
	}
	
	private MinMaxControl criticalControl;
	
	private NumericWeightView statBoostOption;
	private NumericWeightView effectivenessOption;
	private NumericWeightView unbreakableOption;
	private NumericWeightView braveOption;
	private NumericWeightView reverseTriangleOption;
	private NumericWeightView extendedRangeOption;
	private NumericWeightView highCriticalOption;
	private NumericWeightView magicDamageOption;
	private NumericWeightView poisonOption;
	private NumericWeightView stealHPOption;
	private NumericWeightView critImmuneOption;
	private NumericWeightView noCritOption;
	private NumericWeightView eclipseOption;
	private NumericWeightView devilOption;
	
	private List<NumericWeightView> allOptions;
	
	private WeaponEffectSelectionViewListener listener;
	private boolean squelchCallbacks;

	public WeaponEffectSelectionView(Composite parent, GameType type) {
		super(parent, SWT.NONE);
		
		RowLayout rowLayout = new RowLayout();
		setLayout(rowLayout);
		
		buildView(type);
	}
	
	protected void buildView(GameType type) {
		allOptions = new ArrayList<NumericWeightView>();
		
		statBoostOption = new NumericWeightView(this, "Stat Boosts", "Allows random weapons to grant 5 points to a random stat when equipped.", 1, 0, new NumericWeightViewListener() {
			@Override
			public void onValueChanged(int newValue) {
				if (newValue == 0) {
					statBoostOption.disable();
					notifySelectionChange();
				}
				
				updatePercentages();
			}
			
			@Override
			public void onEnableChanged(boolean enabled) {
				notifySelectionChange();
				updatePercentages();
			}
		});
		RowData data = new RowData();
		data.width = 400;
		statBoostOption.setLayoutData(data);
		allOptions.add(statBoostOption);
		
		effectivenessOption = new NumericWeightView(this, "Effectiveness", "Allows random weapons to be effective against certain unit types.", 1, 1, new NumericWeightViewListener() {
			@Override
			public void onEnableChanged(boolean enabled) {
				notifySelectionChange();
				updatePercentages();
			}

			@Override
			public void onValueChanged(int newValue) {
				if (newValue == 0) {
					effectivenessOption.disable();
					notifySelectionChange();
				}
				
				updatePercentages();
			}
			
		});
		effectivenessOption.setLayoutData(data);
		allOptions.add(effectivenessOption);
		
		unbreakableOption = new NumericWeightView(this, "Unbreakable", "Allows random weapons to be have infinite durability.", 1, 1, new NumericWeightViewListener() {
			@Override
			public void onEnableChanged(boolean enabled) {
				notifySelectionChange();
				updatePercentages();
			}

			@Override
			public void onValueChanged(int newValue) {
				if (newValue == 0) {
					unbreakableOption.disable();
					notifySelectionChange();
				}
				
				updatePercentages();
			}
			
		});
		unbreakableOption.setLayoutData(data);
		allOptions.add(unbreakableOption);
		
		braveOption = new NumericWeightView(this, "Brave", "Allows random weapons to strike twice per attack.", 1, 1, new NumericWeightViewListener() {
			@Override
			public void onEnableChanged(boolean enabled) {
				notifySelectionChange();
				updatePercentages();
			}

			@Override
			public void onValueChanged(int newValue) {
				if (newValue == 0) {
					braveOption.disable();
					notifySelectionChange();
				}
				
				updatePercentages();
			}
			
		});
		braveOption.setLayoutData(data);
		allOptions.add(braveOption);
		
		if (type == GameType.FE9) {
			reverseTriangleOption = new NumericWeightView(this, "Shifted Triangle", "Allows random weapons to have a different triangle type from their equip type\\n(e.g. Axes that act like Swords (good against other axes, weak against lances).)", 1, 1, new NumericWeightViewListener() {
				@Override
				public void onEnableChanged(boolean enabled) {
					notifySelectionChange();
					updatePercentages();
				}

				@Override
				public void onValueChanged(int newValue) {
					if (newValue == 0) {
						reverseTriangleOption.disable();
						notifySelectionChange();
					}
					
					updatePercentages();
				}
				
			});
		} else {
			reverseTriangleOption = new NumericWeightView(this, "Reverse Triangle", "Allows random weapons to reverse their Weapon Triangle advantage and disadvantage.", 1, 1, new NumericWeightViewListener() {
				@Override
				public void onEnableChanged(boolean enabled) {
					notifySelectionChange();
					updatePercentages();
				}

				@Override
				public void onValueChanged(int newValue) {
					if (newValue == 0) {
						reverseTriangleOption.disable();
						notifySelectionChange();
					}
					
					updatePercentages();
				}
				
			});
		}
		reverseTriangleOption.setLayoutData(data);
		allOptions.add(reverseTriangleOption);
		
		extendedRangeOption = new NumericWeightView(this, "Extended Range", "Allows random weapons to gain range. In most cases, max range is increased, though bows may gain the ability to be melee weapons. Siege tomes are unaffected.", 1, 1, new NumericWeightViewListener() {
			@Override
			public void onEnableChanged(boolean enabled) {
				notifySelectionChange();
				updatePercentages();
			}

			@Override
			public void onValueChanged(int newValue) {
				if (newValue == 0) {
					extendedRangeOption.disable();
					notifySelectionChange();
				}
				
				updatePercentages();
			}
			
		});
		extendedRangeOption.setLayoutData(data);
		allOptions.add(extendedRangeOption);
		
		highCriticalOption = new NumericWeightView(this, "Critical", "Allows random weapons to gain a large critical bonus.", 1, 1, new NumericWeightViewListener() {
			@Override
			public void onEnableChanged(boolean enabled) {
				notifySelectionChange();
				updatePercentages();
				criticalControl.setEnabled(enabled);
			}

			@Override
			public void onValueChanged(int newValue) {
				if (newValue == 0) {
					highCriticalOption.disable();
					notifySelectionChange();
					criticalControl.setEnabled(false);
				}
				
				updatePercentages();
			}
			
		});
		highCriticalOption.setLayoutData(data);
		allOptions.add(highCriticalOption);
		
		criticalControl = new MinMaxControl(this, SWT.NONE, "", "~");
		criticalControl.getMinSpinner().setValues(20, 5, 50, 0, 5, 5);
		criticalControl.getMaxSpinner().setValues(50, 20, 100, 0, 5, 5);
		criticalControl.setEnabled(false);
		
		if (type == GameType.FE9) {
			magicDamageOption = new NumericWeightView(this, "Magic Damage", "Allows random physical weapons to gain a magic attack that targets RES. Tomes are unaffected.", 1, 1, new NumericWeightViewListener() {
				@Override
				public void onEnableChanged(boolean enabled) {
					notifySelectionChange();
					updatePercentages();
					magicDamageOption.setEnabled(enabled);
				}

				@Override
				public void onValueChanged(int newValue) {
					if (newValue == 0) {
						magicDamageOption.disable();
						notifySelectionChange();
					}
					
					updatePercentages();
				}
				
			});
		} else {
			magicDamageOption = new NumericWeightView(this, "Magic Damage", "Allows random physical weapons to gain a magic attack. Melee weapons gain range if they were not already ranged. Weapons get assigned a random magic animation. Tomes are unaffected.", 1, 1, new NumericWeightViewListener() {
				@Override
				public void onEnableChanged(boolean enabled) {
					notifySelectionChange();
					updatePercentages();
					magicDamageOption.setEnabled(enabled);
				}

				@Override
				public void onValueChanged(int newValue) {
					if (newValue == 0) {
						magicDamageOption.disable();
						notifySelectionChange();
					}
					
					updatePercentages();
				}
				
			});
		}
		magicDamageOption.setLayoutData(data);
		allOptions.add(magicDamageOption);
		
		poisonOption = new NumericWeightView(this, "Poison", "Allows random weapons to apply poison on hit.", 1, 1, new NumericWeightViewListener() {
			@Override
			public void onEnableChanged(boolean enabled) {
				notifySelectionChange();
				updatePercentages();
			}

			@Override
			public void onValueChanged(int newValue) {
				if (newValue == 0) {
					poisonOption.disable();
					notifySelectionChange();
				}
				
				updatePercentages();
			}
			
		});
		poisonOption.setLayoutData(data);
		allOptions.add(poisonOption);
		
		if (type == GameType.FE9) {
			stealHPOption = new NumericWeightView(this, "Steal HP", "Allows random weapons to drain target's HP on hit.\n\nCannot apply to magic tomes.", 1, 1, new NumericWeightViewListener() {
				@Override
				public void onEnableChanged(boolean enabled) {
					notifySelectionChange();
					updatePercentages();
				}

				@Override
				public void onValueChanged(int newValue) {
					if (newValue == 0) {
						stealHPOption.disable();
						notifySelectionChange();
					}
					
					updatePercentages();
				}
				
			});
			stealHPOption.setLayoutData(data);
			allOptions.add(stealHPOption);
			
			critImmuneOption = new NumericWeightView(this, "Crit. Immunity", "Allows random weapons to block non-Wrath critical hits while equipped.", 1, 1, new NumericWeightViewListener() {
				@Override
				public void onEnableChanged(boolean enabled) {
					notifySelectionChange();
					updatePercentages();
				}

				@Override
				public void onValueChanged(int newValue) {
					if (newValue == 0) {
						critImmuneOption.disable();
						notifySelectionChange();
					}
					
					updatePercentages();
				}
				
			});
			critImmuneOption.setLayoutData(data);
			allOptions.add(critImmuneOption);
			
			noCritOption = new NumericWeightView(this, "Disable Crit.", "Disables the ability to trigger critical hits when random weapons are used. Hit is automatically improved by 50 and weapon experience gained per use is increased.", 1, 1, new NumericWeightViewListener() {
				@Override
				public void onEnableChanged(boolean enabled) {
					notifySelectionChange();
					updatePercentages();
				}

				@Override
				public void onValueChanged(int newValue) {
					if (newValue == 0) {
						noCritOption.disable();
						notifySelectionChange();
					}
					
					updatePercentages();
				}
				
			});
			noCritOption.setLayoutData(data);
			allOptions.add(noCritOption);
		}
		
		if (type != GameType.FE6 && type != GameType.FE9) {			
			eclipseOption = new NumericWeightView(this, "Eclipse", "Allows random weapons to always do half of the target's current HP.", 1, 1, new NumericWeightViewListener() {
				@Override
				public void onEnableChanged(boolean enabled) {
					notifySelectionChange();
					updatePercentages();
				}

				@Override
				public void onValueChanged(int newValue) {
					if (newValue == 0) {
						eclipseOption.disable();
						notifySelectionChange();
					}
					
					updatePercentages();
				}
				
			});
			eclipseOption.setLayoutData(data);
			allOptions.add(eclipseOption);
		}
		
		if (type != GameType.FE9) {
			if (type == GameType.FE6) {
				devilOption = new NumericWeightView(this, "Devil", "Allows random weapons to occasionally deal damage to its user instead of its target. Might is automatically improved by at least 5.", 1, 1, new NumericWeightViewListener() {
					@Override
					public void onEnableChanged(boolean enabled) {
						notifySelectionChange();
						updatePercentages();
					}
	
					@Override
					public void onValueChanged(int newValue) {
						if (newValue == 0) {
							devilOption.disable();
							notifySelectionChange();
						}
						
						updatePercentages();
					}
				});
			} else {
				devilOption = new NumericWeightView(this, "Devil", "Allows random weapons to occasionally deal damage to its user instead of its target. Might is automatically improved by at least 5 and weapon experience gained per use is increased.", 1, 1, new NumericWeightViewListener() {
					@Override
					public void onEnableChanged(boolean enabled) {
						notifySelectionChange();
						updatePercentages();
					}
	
					@Override
					public void onValueChanged(int newValue) {
						if (newValue == 0) {
							devilOption.disable();
							notifySelectionChange();
						}
						
						updatePercentages();
					}
				});
			}
			devilOption.setLayoutData(data);
			allOptions.add(devilOption);
		}
		
		updatePercentages();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		for (NumericWeightView option : allOptions) {
			option.setSelectable(enabled);
		}
		
		criticalControl.setEnabled(highCriticalOption.getWeight() > 0);
	}
	
	public void setSelectionListener(WeaponEffectSelectionViewListener weaponEffectSelectionViewListener) {
		this.listener = weaponEffectSelectionViewListener;
	}
	
	public void selectAll() {
		squelchCallbacks = true;
		for (NumericWeightView option : allOptions) {
			option.setWeight(5);
		}
		criticalControl.setEnabled(true);
		squelchCallbacks = false;
		
		notifySelectionChange();
		
		updatePercentages();
	}
	
	public void deselectAll() {
		squelchCallbacks = true;
		
		for (NumericWeightView option : allOptions) {
			option.disable();
		}
		criticalControl.setEnabled(false);
		squelchCallbacks = false;
		
		notifySelectionChange();
		
		updatePercentages();
	}
	
	public void updatePercentages() {
		int totalWeight = allOptions.stream().map(option -> option.getWeight()).reduce(0, (int1, int2) -> int1 + int2);
		allOptions.forEach(option -> option.updateWeightTotal(totalWeight));
	}
	
	public Boolean isAllDisabled() {
		int totalWeight = allOptions.stream().map(option -> option.getWeight()).reduce(0, (int1, int2) -> int1 + int2);
		return totalWeight < 1;
	}
	
	public WeaponEffectOptions getOptions() {
		return new WeaponEffectOptions(statBoostOption.getWeight(), effectivenessOption.getWeight(), unbreakableOption.getWeight(), 
				braveOption.getWeight(), reverseTriangleOption.getWeight(), extendedRangeOption.getWeight(), highCriticalOption.getWeight(), criticalControl.getMinMaxOption(), 
				magicDamageOption.getWeight(), poisonOption.getWeight(), 
				stealHPOption != null ? stealHPOption.getWeight() : 0, 
				critImmuneOption != null ? critImmuneOption.getWeight() : 0,
				noCritOption != null ? noCritOption.getWeight() : 0, 
				eclipseOption != null ? eclipseOption.getWeight() : 0,
				devilOption != null ? devilOption.getWeight() : 0);
	}
	
	public void initialize(WeaponEffectOptions options) {
		if (options != null) {
			statBoostOption.setSelectable(true);
			statBoostOption.setWeight(options.statBoosts);
			
			effectivenessOption.setSelectable(true);
			effectivenessOption.setWeight(options.effectiveness);
			
			unbreakableOption.setSelectable(true);
			unbreakableOption.setWeight(options.unbreakable);
			
			braveOption.setSelectable(true);
			braveOption.setWeight(options.brave);
			
			reverseTriangleOption.setSelectable(true);
			reverseTriangleOption.setWeight(options.reverseTriangle);
			
			extendedRangeOption.setSelectable(true);
			extendedRangeOption.setWeight(options.extendedRange);
			
			highCriticalOption.setSelectable(true);
			highCriticalOption.setWeight(options.highCritical);
			criticalControl.setMin(options.criticalRange.minValue);
			criticalControl.setMax(options.criticalRange.maxValue);
			criticalControl.setEnabled(options.highCritical > 0);
			
			magicDamageOption.setSelectable(true);
			magicDamageOption.setWeight(options.magicDamage);
			
			poisonOption.setSelectable(true);
			poisonOption.setWeight(options.poison);
			
			if (stealHPOption != null) {
				stealHPOption.setSelectable(true);
				stealHPOption.setWeight(options.stealHP);
			}
			
			if (critImmuneOption != null) {
				critImmuneOption.setSelectable(true);
				critImmuneOption.setWeight(options.critImmune);
			}
			
			if (noCritOption != null) {
				noCritOption.setSelectable(true);
				noCritOption.setWeight(options.noCrit);
			}
			
			if (eclipseOption != null) {
				eclipseOption.setSelectable(true);
				eclipseOption.setWeight(options.eclipse);
			}
			
			if (devilOption != null) {
				devilOption.setSelectable(true);
				devilOption.setWeight(options.devil);
			}
			
			notifySelectionChange();
			updatePercentages();
		}
	}
	
	public void notifySelectionChange() {
		if (!squelchCallbacks && listener != null) {
			listener.onSelectionChanged();
		}
	}
}
