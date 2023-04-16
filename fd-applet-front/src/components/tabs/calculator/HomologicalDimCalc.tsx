import { useState } from "react";

import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Autocomplete,
  Divider,
  Grid,
  TextField,
  Typography,
} from "@mui/material";

import useFetchWithUiFeedback from "../../../hooks/useFetchWithUiFeedback";
import ComputeButton from "../../common/ComputeButton";
import { Pair, PairTable } from "../../common/PairTable";

import CalcProps from "./CalcProps";

export default function HomologicalDimCalc({ options }: CalcProps) {
  const fetchWithUiFeedback = useFetchWithUiFeedback();

  const [mXX, setmXX] = useState<string[]>([]);
  const [valueX, setValueX] = useState("");
  const [result, setResult] = useState<Pair<string, number | null>[]>([]);

  async function getDim() {
    const actualXX = mXX;
    if (valueX !== "") {
      setmXX(mXX.concat(valueX));
      actualXX.push(valueX);
      setValueX("");
    }

    const response = await fetchWithUiFeedback<Pair<string, number | null>[]>({
      url: "/api/calculator/1/dim",
      method: "POST",
      body: actualXX,
    });
    if (response.data === undefined) return;
    setResult(response.data);
  }

  return (
    <Accordion>
      <AccordionSummary expandIcon={<ExpandMoreIcon />}>
        <Typography>Homological dimensions</Typography>
      </AccordionSummary>
      <Divider />
      <AccordionDetails>
        <Typography variant="subtitle1">
          Compute various homological dimensions of X.
        </Typography>
        <Grid
          container
          spacing={1}
          mt={1}
          alignItems="center"
          justifyContent="center"
          display="flex"
          sx={{ width: "auto" }}
        >
          <Grid item xs={10}>
            <Autocomplete
              freeSolo
              multiple
              value={mXX}
              onChange={(event: unknown, newValue: string[]) => {
                setmXX(newValue);
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
            <ComputeButton onClick={getDim} />
          </Grid>
          <Grid item xs={12}>
            <PairTable
              firstName="Descriptions"
              secondName="values"
              data={result}
            />
          </Grid>
        </Grid>
      </AccordionDetails>
    </Accordion>
  );
}
