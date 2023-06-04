import { useEffect, useState } from "react";

import AutoFixHighIcon from '@mui/icons-material/AutoFixHigh';
import CalculateOutlinedIcon from '@mui/icons-material/CalculateOutlined';
import FormatListNumberedIcon from '@mui/icons-material/FormatListNumbered';
import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined';
import LinearScaleIcon from '@mui/icons-material/LinearScale';
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
import useFetchWithUiFeedback from "../hooks/useFetchWithUiFeedback";

import GenericQuiver from "./common/GenericQuiver";
import { defaultPhysicsOptions as defaultPhysicsOption } from "./common/QuiverOptions";
import AlgebraInfoTab from "./tabs/AlgebraInfo";
import ArMenu from "./tabs/arQuiver/ArMenu";
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
  const { selected, setSelected, secondarySelected, setSecondarySelected, highlighted, setHighlighted } = useSelection();
  const fetchWithUiFeedback = useFetchWithUiFeedback();

  const [tabValue, setTabValue] = useState(0);
  const [showAR, setShowAR] = useState(true);

  const [physics, setPhysics] = useState<unknown>(defaultPhysicsOption);

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

  // Currently async functions, so color change is not immediate.
  // TODO: make it immediate (in <GenericQuiver> component?).
  async function handleARComputed() {
    handleComputationUpdate(5)(true);
    const responseProj = await fetchWithUiFeedback<string[]>({
      url: "/api/indec/proj",
      showSuccess: false,
    });
    const responseInj = await fetchWithUiFeedback<string[]>({
      url: "/api/indec/inj",
      showSuccess: false,
    });
    if (responseProj.data === undefined) return;
    if (responseInj.data === undefined) return;
    setSecondarySelected(responseProj.data);
    setSelected(responseInj.data);
  }

  useEffect(() => {
    if (!showAR) {
      setSelected([]);
      setSecondarySelected([]);
      setHighlighted([]);
    }
  }, [showAR]);

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
          <Tab
            icon={<InfoOutlinedIcon />}
            label="Info"
            sx={{ textTransform: 'none' }} />
          <Tab
            icon={<CalculateOutlinedIcon />}
            label="Calculator"
            sx={{ textTransform: 'none' }} />
          <Tab
            icon={<FormatListNumberedIcon />}
            label="Enumerator" sx={{ textTransform: 'none' }} />
          <Tab
            icon={<AutoFixHighIcon />}
            label="Converter" sx={{ textTransform: 'none' }} />
          <Tab
            icon={<LinearScaleIcon />}
            label="Quivers" sx={{ textTransform: 'none' }} />
          <Tab sx={{ textTransform: 'none' }}
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
                labelPlacement="bottom"
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
              <ArMenu
                physics={physics}
                setPhysics={setPhysics}
              />
              <GenericQuiver
                buttonTitle="Compute AR quiver"
                url="/api/quiver/ar"
                physicOption={physics}
                selected={selected}
                secondarySelected={secondarySelected}
                highlighted={highlighted}
                hide={!computationState[5]}
                computationCallback={handleARComputed}
              />
            </Box>
          </Grid>
        )}
      </Grid>
    </Box>
  );
}
