import { useState } from "react";

import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Grid,
  TextField,
  Typography,
} from "@mui/material";

import { useSelection } from "../../../contexts/SelectionContext";
import { useUi } from "../../../contexts/UiContext";
import useFetchWithUiFeedback from "../../../hooks/useFetchWithUiFeedback";
import { Combo, ComboOption } from "../../common/Combo";
import ComputeButton from "../../common/ComputeButton";
import ListWithIndecList from "../../common/ListWithIndecList";


const moduleWithDegreeOptions: ComboOption[] = [
  { key: "cluster_tilt", label: "n-cluster tilting modules" },
  { key: "-", label: "-" },
  { key: "n_tilt", label: "Tilting modules with pd <= n" },
  { key: "n_cotilt", label: "Cotilting modules with id <= n" },
];

export default function ModuleWithDegreeEnumerator() {
  const { setOpenNotify, setNotifyStatus } = useUi();
  const fetchWithUiFeedback = useFetchWithUiFeedback();
  const { setSelected, setSecondarySelected, setHighlighted } = useSelection();

  const [selectedMenu, setSelectedMenu] = useState("cluster_tilt");
  const [data, setData] = useState<string[][]>([]);

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
      showDuration: true
    });
    if (response.data === undefined) return;
    setData(response.data);
    setSelected([]);
    setSecondarySelected([]);
    setHighlighted([]);
  }

  return (
    <Accordion>
      <AccordionSummary expandIcon={<ExpandMoreIcon />}>
        <Typography fontWeight="medium">Modules with degree (n-cluster tilting, ...)</Typography>
      </AccordionSummary>
      <AccordionDetails>
        <Grid
          container
          spacing={2}
          mb={2}
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
          <Grid item xs={2} display="flex" justifyContent="center">
            <ComputeButton onClick={getData} />
          </Grid>
        </Grid>
        <ListWithIndecList
          candidates={data}
          leftHeader="Modules"
          handleChange={(value) => {
            setSelected(value);
            setSecondarySelected([]);
            setHighlighted([]);
          }}
        />
      </AccordionDetails>
    </Accordion>
  );
}
