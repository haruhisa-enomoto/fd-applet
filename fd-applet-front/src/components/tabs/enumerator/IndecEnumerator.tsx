import { useState } from "react";

import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Grid,
  Typography,
} from "@mui/material";

import { useSelection } from "../../../contexts/SelectionContext";
import useFetchWithUiFeedback from "../../../hooks/useFetchWithUiFeedback";
import { Combo, ComboOption } from "../../common/Combo";
import ComputeButton from "../../common/ComputeButton";
import LargeList from "../../common/LargeList";

const indecOptions: ComboOption[] = [
  { key: "all", label: "All modules" },
  { key: "-", label: "-" },
  { key: "proj", label: "Projective modules" },
  { key: "inj", label: "Injective modules" },
  { key: "simple", label: "Simple modules" },
  { key: "-", label: "-" },
  { key: "brick", label: "Bricks" },
  { key: "-", label: "-" },
  { key: "fpd", label: "Modules with finite proj. dim." },
  { key: "fid", label: "Modules with finite inj. dim." },
  { key: "-", label: "-" },
  { key: "gp", label: "Gorenstein-projective modules" },
  { key: "refl", label: "Reflexive modules" },
  { key: "inf_torsless", label: "∞-torsionless modules" },
];

export default function IndecEnumerator() {
  const fetchWithUiFeedback = useFetchWithUiFeedback();
  const { setSelected, setSecondarySelected, setHighlighted } = useSelection();

  const [selectedMenu, setSelectedMenu] = useState("all");
  const [data, setData] = useState<string[]>([]);

  async function getData() {
    const response = await fetchWithUiFeedback<string[]>({
      url: "/api/indec/" + selectedMenu,
    });
    if (response.data === undefined) return;
    setData(response.data);
    setSelected(response.data);
    setSecondarySelected([]);
    setHighlighted([]);
  }

  return (
    <Accordion>
      <AccordionSummary expandIcon={<ExpandMoreIcon />}>
        <Typography fontWeight="medium">Indecomposable modules (bricks, ...)</Typography>
      </AccordionSummary>
      <AccordionDetails>
        <Grid
          container
          spacing={2}
          mb={2}
          justifyContent="space-around"
          alignItems="center"
        >
          <Grid item xs={10}>
            <Combo
              title="Choose types"
              options={indecOptions}
              selected={selectedMenu}
              setSelected={setSelectedMenu}
            />
          </Grid>
          <Grid item xs={2} display="flex" justifyContent="center">
            <ComputeButton onClick={getData} />
          </Grid>
        </Grid>
        <LargeList
          header="Indecs"
          data={data}
          onSelect={(index) => {
            setHighlighted([data[index]]);
          }}
        />
      </AccordionDetails>
    </Accordion>
  );
}
