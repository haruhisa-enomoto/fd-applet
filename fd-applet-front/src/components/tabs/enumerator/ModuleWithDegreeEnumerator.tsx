import { useState } from "react";

import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Divider,
  Grid,
  TextField,
  Typography,
} from "@mui/material";

import { useSelection } from "../../../contexts/SelectionContext";
import { useUi } from "../../../contexts/UiContext";
import useFetchWithUiFeedback from "../../../hooks/useFetchWithUiFeedback";
import { Combo, ComboOption } from "../../common/Combo";
import ComputeButton from "../../common/ComputeButton";
import LargeList from "../../common/LargeList";

import StatisticsDialog from "./StatisticsDialog";

const moduleWithDegreeOptions: ComboOption[] = [
  { key: "cluster-tilt", label: "n-cluster tilting modules" },
  { key: "-", label: "-" },
  { key: "n-tilt", label: "Tilting modules with pd <= n" },
  { key: "n-cotilt", label: "Cotilting modules with id <= n" },
];

export default function ModuleWithDegreeEnumerator() {
  const { setOpenNotify, setNotifyStatus } = useUi();
  const fetchWithUiFeedback = useFetchWithUiFeedback();
  const { setSelected, setSecondarySelected, setHighlighted } = useSelection();

  const [selectedMenu, setSelectedMenu] = useState("cluster-tilt");
  const [data, setData] = useState<string[][]>([]);
  const [index, setIndex] = useState<number>(0);
  const [degree, setDegree] = useState("");

  async function getData() {
    const n = parseInt(degree);
    if (isNaN(n)) {
      setNotifyStatus({
        message: "n must be an integer.",
        severity: "error",
      });
      setOpenNotify(true);
      return;
    }
    const response = await fetchWithUiFeedback<string[][]>({
      url: "/api/module/n/" + selectedMenu,
      method: "POST",
      body: n,
    });
    if (response.data === undefined) return;
    setData(response.data);
    setSelected([]);
    setSecondarySelected([]);
    setHighlighted([]);
  }

  return (
    <Accordion>
      <AccordionSummary expandIcon={<ExpandMoreIcon />} sx={{ m: 0 }}>
        <Typography>Modules with degree (n-cluster tilting, ...)</Typography>
      </AccordionSummary>
      <Divider />
      <AccordionDetails>
        <Grid
          container
          spacing={2}
          justifyContent="space-around"
          alignItems="center"
        >
          <Grid item xs={8}>
            <Combo
              title="Choose types"
              options={moduleWithDegreeOptions}
              selected={selectedMenu}
              setSelected={setSelectedMenu}
            />
          </Grid>
          <Grid item xs={2}>
            <TextField
              value={degree}
              onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                setDegree(event.target.value);
              }}
              label="n"
            />
          </Grid>
          <Grid item xs={2}>
            <ComputeButton onClick={getData} />
          </Grid>
        </Grid>
        <Grid container spacing={2}>
          <Grid item xs={6}>
            <LargeList
              header="Modules"
              data={data.map((modules) => modules.join(", "))}
              onSelect={(index) => {
                setSelected(data[index]);
                setIndex(index);
              }}
            />
          </Grid>{" "}
          <Grid item xs={6}>
            {data[index] && (
              <LargeList header="Indecs in it" data={data[index]} text />
            )}
          </Grid>
          <Grid item xs={12}>
            {data && <StatisticsDialog data={data} />}
          </Grid>
        </Grid>
      </AccordionDetails>
    </Accordion>
  );
}
