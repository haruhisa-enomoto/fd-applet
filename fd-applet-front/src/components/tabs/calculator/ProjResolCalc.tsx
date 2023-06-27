import { useState } from "react";

import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Autocomplete,
  Grid,
  TextField,
  Typography,
} from "@mui/material";

import { useSelection } from "../../../contexts/SelectionContext";
import { useUi } from "../../../contexts/UiContext";
import useFetchWithUiFeedback from "../../../hooks/useFetchWithUiFeedback";
import ComputeButton from "../../common/ComputeButton";
import { Pair, PairTable } from "../../common/PairTable";

import CalcProps from "./CalcProps";

type ResolutionData = {
  first: string[];
  second: string[];
}[];

export default function ProjResolCalc({ options }: CalcProps) {
  const { setOpenNotify, setNotifyStatus } = useUi();
  const fetchWithUiFeedback = useFetchWithUiFeedback();
  const { setSelected, setSecondarySelected, setHighlighted } = useSelection();


  const [mXX, setmXX] = useState<string[]>([]);
  const [valueX, setValueX] = useState("");
  const [degree, setDegree] = useState<string>("0");
  const [result, setResult] = useState<Pair<string, string>[]>([]);

  async function getProjResol() {
    const actualXX = mXX;
    if (valueX !== "") {
      setmXX(mXX.concat(valueX));
      actualXX.push(valueX);
      setValueX("");
    }
    const n = parseInt(degree);
    if (isNaN(n) || n < 0) {
      setNotifyStatus({
        message: "n must be a non-negative integer.",
        severity: "error",
      });
      setOpenNotify(true);
      return;
    }

    const response = await fetchWithUiFeedback<ResolutionData>({
      url: "/api/calculator/1/resol/proj",
      method: "POST",
      body: { first: actualXX, second: n },
    });
    if (response.data === undefined) return;
    const processed: Pair<string, string>[] = response.data
      .map((element, index) => [
        {
          first: `P_${index}`,
          second: element.first.map((str) => `P(${str})`).join(" ⊕ "),
        },
        {
          first: `Ω^${index + 1} (X)`,
          second:
            element.second.length !== 0 ? element.second.join(" ⊕ ") : "zero",
        },
      ])
      .flat();
    setResult(processed);
    setSelected(actualXX);
    setSecondarySelected([]);
    setHighlighted([]);
  }

  return (
    <Accordion>
      <AccordionSummary expandIcon={<ExpandMoreIcon />}>
        <Typography fontWeight="medium">Projective resolution & syzygy</Typography>
      </AccordionSummary>
      <AccordionDetails>
        <Typography mb={2}>
          Compute the minimal projective resolution of X of the form:
          <br />
          {"0 → Ω^{n+1} → P_n → … → P_1 → P_0 → X → 0"}.
        </Typography>
        <Grid
          container
          spacing={1}
          mb={2}
        >
          <Grid item xs={8}>
            <Autocomplete
              multiple
              value={mXX}
              onChange={(_, newValue: string[]) => {
                setmXX(newValue);
                setSelected(newValue);
                setSecondarySelected([]);
                setHighlighted([]);
              }}
              inputValue={valueX}
              onInputChange={(event, newInputValue) => {
                setValueX(newInputValue);
              }}
              options={options}
              sx={{ width: "auto" }}
              renderInput={(params) => <TextField {...params} label="X" />}
            />
          </Grid>
          <Grid item xs={2}>
            <TextField
              value={degree}
              onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                setDegree(event.target.value);
              }}
              label="n"
              fullWidth
            // onKeyDown={handleKeyDown}
            />
          </Grid>
          <Grid item xs={2} display="flex" justifyContent="center">
            <ComputeButton onClick={getProjResol} />
          </Grid>
          <Grid item xs={12}>
            <PairTable firstName="Term" secondName="Data" data={result} />
          </Grid>
        </Grid>
      </AccordionDetails>
    </Accordion>
  );
}
