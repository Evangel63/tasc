package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import data.campaign.econ.boggledTools;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

public class BoggledTascPlugin extends BaseModPlugin
{
    private static final Logger log = Global.getLogger(BoggledTascPlugin.class);

    public void applyStationSettingsToAllStationsInSector()
    {
        if(boggledTools.getBooleanSetting("boggledApplyStationSettingsToAllStationsInSector"))
        {
            for (StarSystemAPI system : Global.getSector().getStarSystems()) {
                for (MarketAPI market : Global.getSector().getEconomy().getMarkets(system)) {
                    SectorEntityToken primaryEntity = market.getPrimaryEntity();
                    if (primaryEntity != null && primaryEntity.hasTag(Tags.STATION)) {
                        //Cramped Quarters also controls global hazard and accessibility modifications
                        //even if Cramped Quarters itself is disabled
                        if (!market.hasCondition(boggledTools.BoggledConditions.crampedQuartersConditionID)) {
                            market.addCondition(boggledTools.BoggledConditions.crampedQuartersConditionID);
                        }

                        //Some special items require "no_atmosphere" condition on market to be installed
                        //Stations by default don't meet this condition because they don't have the "no_atmosphere" condition
                        //Combined with market_conditions.csv overwrite, this will give stations no_atmosphere while
                        //hiding all effects from the player and having no impact on the economy or hazard rating
                        if (!market.hasCondition(Conditions.NO_ATMOSPHERE)) {
                            market.addCondition(Conditions.NO_ATMOSPHERE);
                            market.suppressCondition(Conditions.NO_ATMOSPHERE);
                        }
                    }
                }
            }
        }
    }

    public void applyTerraformingAbilitiesPerSettingsFile()
    {
        if(boggledTools.getBooleanSetting(boggledTools.BoggledSettings.terraformingContentEnabled))
        {
            if (!Global.getSector().getPlayerFleet().hasAbility("boggled_open_terraforming_control_panel"))
            {
                Global.getSector().getCharacterData().addAbility("boggled_open_terraforming_control_panel");
            }
        }
        else
        {
            Global.getSector().getCharacterData().removeAbility("boggled_open_terraforming_control_panel");
        }
    }

    public void applyStationConstructionAbilitiesPerSettingsFile()
    {
        if(boggledTools.getBooleanSetting(boggledTools.BoggledSettings.stationConstructionContentEnabled))
        {
            if (!Global.getSector().getPlayerFleet().hasAbility("boggled_construct_astropolis_station"))
            {
                if(boggledTools.getBooleanSetting(boggledTools.BoggledSettings.astropolisEnabled))
                {
                    Global.getSector().getCharacterData().addAbility("boggled_construct_astropolis_station");
                }
            }
            else
            {
                if(!boggledTools.getBooleanSetting(boggledTools.BoggledSettings.astropolisEnabled))
                {
                    Global.getSector().getCharacterData().removeAbility("boggled_construct_astropolis_station");
                }
            }

            if (!Global.getSector().getPlayerFleet().hasAbility("boggled_construct_mining_station"))
            {
                if(boggledTools.getBooleanSetting(boggledTools.BoggledSettings.miningStationEnabled))
                {
                    Global.getSector().getCharacterData().addAbility("boggled_construct_mining_station");
                }
            }
            else
            {
                if(!boggledTools.getBooleanSetting(boggledTools.BoggledSettings.miningStationEnabled))
                {
                    Global.getSector().getCharacterData().removeAbility("boggled_construct_mining_station");
                }
            }

            if (!Global.getSector().getPlayerFleet().hasAbility("boggled_construct_siphon_station"))
            {
                if(boggledTools.getBooleanSetting(boggledTools.BoggledSettings.siphonStationEnabled))
                {
                    Global.getSector().getCharacterData().addAbility("boggled_construct_siphon_station");
                }
            }
            else
            {
                if(!boggledTools.getBooleanSetting(boggledTools.BoggledSettings.siphonStationEnabled))
                {
                    Global.getSector().getCharacterData().removeAbility("boggled_construct_siphon_station");
                }
            }

            if (!Global.getSector().getPlayerFleet().hasAbility("boggled_colonize_abandoned_station"))
            {
                if(boggledTools.getBooleanSetting(boggledTools.BoggledSettings.stationColonizationEnabled))
                {
                    Global.getSector().getCharacterData().addAbility("boggled_colonize_abandoned_station");
                }
            }
            else
            {
                if(!boggledTools.getBooleanSetting(boggledTools.BoggledSettings.stationColonizationEnabled))
                {
                    Global.getSector().getCharacterData().removeAbility("boggled_colonize_abandoned_station");
                }
            }
        }
        else
        {
            Global.getSector().getCharacterData().removeAbility("boggled_construct_astropolis_station");
            Global.getSector().getCharacterData().removeAbility("boggled_construct_mining_station");
            Global.getSector().getCharacterData().removeAbility("boggled_construct_siphon_station");
            Global.getSector().getCharacterData().removeAbility("boggled_colonize_abandoned_station");
        }
    }

    public void applyDomainArchaeologySettings()
    {
        //Enable/disable Domain-tech content
        if(boggledTools.getBooleanSetting(boggledTools.BoggledSettings.domainTechContentEnabled) && boggledTools.getBooleanSetting(boggledTools.BoggledSettings.domainArchaeologyEnabled))
        {
            if(Global.getSector().getFaction(Factions.LUDDIC_CHURCH) != null && !Global.getSector().getFaction(Factions.LUDDIC_CHURCH).isIllegal(boggledTools.BoggledCommodities.domainArtifacts))
            {
                Global.getSector().getFaction(Factions.LUDDIC_CHURCH).makeCommodityIllegal(boggledTools.BoggledCommodities.domainArtifacts);
            }

            if(Global.getSector().getFaction(Factions.LUDDIC_PATH) != null && !Global.getSector().getFaction(Factions.LUDDIC_PATH).isIllegal(boggledTools.BoggledCommodities.domainArtifacts))
            {
                Global.getSector().getFaction(Factions.LUDDIC_PATH).makeCommodityIllegal(boggledTools.BoggledCommodities.domainArtifacts);
            }

            Global.getSettings().getCommoditySpec(boggledTools.BoggledCommodities.domainArtifacts).getTags().clear();

            if(boggledTools.getBooleanSetting(boggledTools.BoggledSettings.replaceAgreusTechMiningWithDomainArchaeology))
            {
                SectorEntityToken agreusPlanet = boggledTools.getPlanetTokenForQuest("Arcadia", "agreus");
                if(agreusPlanet != null)
                {
                    MarketAPI agreusMarket = agreusPlanet.getMarket();
                    if(agreusMarket != null && agreusMarket.hasIndustry(Industries.TECHMINING) && !agreusMarket.hasIndustry(boggledTools.BoggledIndustries.domainArchaeologyIndustryID) && !agreusMarket.isPlayerOwned())
                    {
                        // See boggledAgreusTechMiningEveryFrameScript for solution to Agreus Everybody loves KoC Techmining/Domain Archaeology issue
                        if(!Global.getSettings().getModManager().isModEnabled("Everybody loves KoC"))
                        {
                            agreusMarket.addIndustry(boggledTools.BoggledIndustries.domainArchaeologyIndustryID);
                            agreusMarket.removeIndustry(Industries.TECHMINING, null, false);
                        }
                        else
                        {
                            Global.getSector().addTransientScript(new boggledAgreusTechMiningEveryFrameScript());
                        }
                    }
                }
            }
        }
        else
        {
            Global.getSettings().getCommoditySpec(boggledTools.BoggledCommodities.domainArtifacts).getTags().add("nonecon");
        }
    }

    public void addDomainTechBuildingsToVanillaColonies()
    {
        // Check to avoid null pointer exception if player has modified/randomized sector
        if(Global.getSector() == null || Global.getSector().getStarSystem("Askonia") == null)
        {
            return;
        }

        if(!Global.getSector().getPlayerPerson().hasTag("boggledDomainTechBuildingPlacementFinished"))
        {
            // Add Genelab on Volturn
            // Add LLN on Fikenhild
            // Add GPA on Ancyra
            if(boggledTools.getBooleanSetting(boggledTools.BoggledSettings.domainTechContentEnabled) && boggledTools.getBooleanSetting(boggledTools.BoggledSettings.domainArchaeologyEnabled) && boggledTools.getBooleanSetting(boggledTools.BoggledSettings.addDomainTechBuildingsToVanillaColonies))
            {
                SectorEntityToken volturnPlanet = boggledTools.getPlanetTokenForQuest("Askonia", "volturn");
                if(volturnPlanet != null)
                {
                    MarketAPI volturnMarket = volturnPlanet.getMarket();
                    if(volturnMarket != null && !volturnMarket.hasIndustry(boggledTools.BoggledIndustries.genelabIndustryID))
                    {
                        volturnMarket.addIndustry(boggledTools.BoggledIndustries.genelabIndustryID);
                    }
                }

                SectorEntityToken fikenhildPlanet = boggledTools.getPlanetTokenForQuest("Westernesse", "fikenhild");
                if(fikenhildPlanet != null)
                {
                    MarketAPI fikenhildMarket = fikenhildPlanet.getMarket();
                    if(fikenhildMarket != null && !fikenhildMarket.hasIndustry("BOGGLED_LIMELIGHT_NETWORK"))
                    {
                        fikenhildMarket.addIndustry("BOGGLED_LIMELIGHT_NETWORK");
                    }
                }

                SectorEntityToken ancyraPlanet = boggledTools.getPlanetTokenForQuest("Galatia", "ancyra");
                if(ancyraPlanet != null)
                {
                    MarketAPI ancyraMarket = ancyraPlanet.getMarket();
                    if(ancyraMarket != null && !ancyraMarket.hasIndustry("BOGGLED_GPA"))
                    {
                        ancyraMarket.addIndustry("BOGGLED_GPA");
                    }
                }
            }

            Global.getSector().getPlayerPerson().addTag("boggledDomainTechBuildingPlacementFinished");
        }
    }

    public void replaceCryosanctums()
    {
        // Replace all Cryosanctums
        if(!Global.getSector().getPlayerPerson().hasTag("boggledCryosanctumReplacementFinished") && boggledTools.getBooleanSetting(boggledTools.BoggledSettings.domainTechContentEnabled) && boggledTools.getBooleanSetting(boggledTools.BoggledSettings.domainArchaeologyEnabled) && boggledTools.getBooleanSetting(boggledTools.BoggledSettings.cryosanctumReplaceEverywhere))
        {
            for(StarSystemAPI system : Global.getSector().getStarSystems())
            {
                for(MarketAPI market : Global.getSector().getEconomy().getMarkets(system))
                {
                    if(market != null && market.hasIndustry(Industries.CRYOSANCTUM) && !market.hasIndustry(boggledTools.BoggledIndustries.cryosanctumIndustryID))
                    {
                        market.removeIndustry(Industries.CRYOSANCTUM, null, false);
                        market.addIndustry(boggledTools.BoggledIndustries.cryosanctumIndustryID);
                    }
                }
            }

            Global.getSector().getPlayerPerson().addTag("boggledCryosanctumReplacementFinished");
        }
    }

    public void enablePlanetKiller()
    {
        if(boggledTools.getBooleanSetting(boggledTools.BoggledSettings.domainTechContentEnabled) && boggledTools.getBooleanSetting(boggledTools.BoggledSettings.planetKillerEnabled))
        {
            // PK weapons are deployed via ability, not a ground raid.
            // I left the mostly finished code for ground raid deployment in here in case I want to enable it in a future update.
            // Global.getSector().getListenerManager().addListener(new boggledPlanetKillerGroundRaidObjectiveListener());

            Global.getSector().getCharacterData().addAbility("boggled_deploy_planet_killer");
        }
    }

    @Override
    public void onNewGame()
    {
        applyStationSettingsToAllStationsInSector();
    }

    public void afterGameSave()
    {
        enablePlanetKiller();

        applyStationSettingsToAllStationsInSector();

        applyStationConstructionAbilitiesPerSettingsFile();

        applyTerraformingAbilitiesPerSettingsFile();

        applyDomainArchaeologySettings();

        replaceCryosanctums();

        addDomainTechBuildingsToVanillaColonies();
    }

    public void beforeGameSave()
    {
        Global.getSector().getCharacterData().removeAbility("boggled_construct_astropolis_station");
        Global.getSector().getCharacterData().removeAbility("boggled_construct_mining_station");
        Global.getSector().getCharacterData().removeAbility("boggled_construct_siphon_station");
        Global.getSector().getCharacterData().removeAbility("boggled_colonize_abandoned_station");

        Global.getSector().getCharacterData().removeAbility("boggled_deploy_planet_killer");

        Global.getSector().getCharacterData().removeAbility("boggled_open_terraforming_control_panel");

        Global.getSettings().getCommoditySpec(boggledTools.BoggledCommodities.domainArtifacts).getTags().clear();

        Global.getSector().getListenerManager().removeListenerOfClass(boggledPlanetKillerGroundRaidObjectiveListener.class);
    }

    public void onGameLoad(boolean newGame)
    {
        try {
//            JSONArray planetTypesMap = Global.getSettings().getMergedSpreadsheetDataForMod("id", "data/campaign/planet_types.csv", boggledTools.BoggledMods.tascModID);
            JSONArray terraformingRequirement = Global.getSettings().getMergedSpreadsheetDataForMod("id", "data/campaign/terraforming_requirement.csv", boggledTools.BoggledMods.tascModID);
            JSONArray terraformingRequirements = Global.getSettings().getMergedSpreadsheetDataForMod("id", "data/campaign/terraforming_requirements.csv", boggledTools.BoggledMods.tascModID);
            JSONArray terraformingProjects = Global.getSettings().getMergedSpreadsheetDataForMod("id", "data/campaign/terraforming_projects.csv", boggledTools.BoggledMods.tascModID);

            boggledTools.initialiseTerraformingRequirementFromJSON(terraformingRequirement);

            boggledTools.initialiseTerraformingRequirementsFromJSON(terraformingRequirements);

            boggledTools.initialiseTerraformingProjectsFromJSON(terraformingProjects);

        } catch (IOException | JSONException ex) {
            log.error(ex);
        }

        enablePlanetKiller();

        applyStationSettingsToAllStationsInSector();

        applyStationConstructionAbilitiesPerSettingsFile();

        applyTerraformingAbilitiesPerSettingsFile();

        applyDomainArchaeologySettings();

        addDomainTechBuildingsToVanillaColonies();
    }
}