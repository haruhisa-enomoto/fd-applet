import { useState } from "react";

import {
  Box,
  FormControlLabel,
  Grid,
  Switch,
  Tab,
  Tabs,
  Typography,
} from "@mui/material";

import { ComputationState } from "../App";
import { useSelection } from "../contexts/SelectionContext";

import GenericQuiver from "./common/GenericQuiver";
import AlgebraInfoTab from "./tabs/AlgebraInfo";
import CalculatorTab from "./tabs/calculator/CalculatorTab";
import ConverterTab from "./tabs/converter/ConverterTab";
import EnumerateTab from "./tabs/enumerator/EnumeratorTab";
import QuiversTab from "./tabs/quivers/QuiversTab";

interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
}

function TabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props;

  return (
    <div role="tabpanel" hidden={value !== index} {...other}>
      <Box sx={{ p: 1 }}>{children}</Box>
    </div>
  );
}

interface MainTabsProps {
  computationState: ComputationState;
  handleComputationUpdate: (tabIndex: number) => (newState: boolean) => void;
}

export default function MyTabs({
  computationState,
  handleComputationUpdate,
}: MainTabsProps) {
  const { selected, secondarySelected, highlighted } = useSelection();

  const [tabValue, setTabValue] = useState(0);
  const [showAR, setShowAR] = useState(true);

  const handleChange = (_event: React.SyntheticEvent, newValue: number) => {
    // For "AR Quiver" switch.
    if (newValue !== 5) {
      setTabValue(newValue);
    } else {
      setShowAR(!showAR);
    }
  };

  const handleARChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setShowAR(event.target.checked);
  };

  return (
    <Box sx={{ width: "auto" }}>
      <Box sx={{ borderBottom: 1, borderColor: "divider" }}>
        <Tabs
          value={tabValue}
          onChange={handleChange}
          variant="scrollable"
          scrollButtons
          action={(actions) => {
            if (actions) {
              actions.updateIndicator();
              actions.updateScrollButtons();
            }
          }}
        >
          <Tab label="Basic Info" />
          <Tab label="Calculator" />
          <Tab label="Enumerator" />
          <Tab label="Converter" />
          <Tab label="Quivers" />
          <Tab
            label={
              <FormControlLabel
                control={
                  <Switch
                    size="small"
                    checked={showAR}
                    onChange={(e) => {
                      e.stopPropagation();
                      handleARChange(e);
                    }}
                  />
                }
                label={<Typography variant="subtitle2">AR quiver</Typography>}
                onClick={(e) => e.stopPropagation()}
              />
            }
          />
        </Tabs>
      </Box>
      <Grid container spacing={2}>
        <Grid item xs={showAR ? 6 : 12}>
          <TabPanel value={tabValue} index={0}>
            <AlgebraInfoTab
              isComputed={computationState[0]}
              setIsComputed={handleComputationUpdate(0)}
            />
          </TabPanel>
          <TabPanel value={tabValue} index={1}>
            <CalculatorTab
              isComputed={computationState[1]}
              setIsComputed={handleComputationUpdate(1)}
            />
          </TabPanel>
          <TabPanel value={tabValue} index={2}>
            <EnumerateTab
              isComputed={computationState[2]}
              setIsComputed={handleComputationUpdate(2)}
            />
          </TabPanel>
          <TabPanel value={tabValue} index={3}>
            <ConverterTab
              isComputed={computationState[3]}
              setIsComputed={handleComputationUpdate(3)}
            />
          </TabPanel>
          <TabPanel value={tabValue} index={4}>
            <QuiversTab
              isComputed={computationState[4]}
              setIsComputed={handleComputationUpdate(4)}
            />
          </TabPanel>
        </Grid>
        {showAR && (
          <Grid item xs={6} mb={1}>
            <Box style={{ position: "sticky", top: "70px" }} sx={{ p: 1 }}>
              <GenericQuiver
                buttonTitle="Compute AR quiver"
                url="/api/quiver/ar"
                selected={selected}
                secondarySelected={secondarySelected}
                highlighted={highlighted}
                hide={!computationState[4]}
                computationCallback={() => handleComputationUpdate(4)(true)}
              />
            </Box>
          </Grid>
        )}
      </Grid>
    </Box>
  );
}
