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

import CalcProps from "./CalcProps";

export default function ExtCalc({ options }: CalcProps) {
  const { setOpenNotify, setNotifyStatus } = useUi();
  const fetchWithUiFeedback = useFetchWithUiFeedback();
  const { setSelected, setSecondarySelected, setHighlighted } = useSelection();

  const [mXX, setmXX] = useState<string[]>([]);
  const [valueX, setValueX] = useState("");
  const [mYY, setmYY] = useState<string[]>([]);
  const [valueY, setValueY] = useState("");
  const [degree, setDegree] = useState<string>("0");
  const [result, setResult] = useState<number>();

  const [computeClicked, setComputeClicked] = useState<boolean>(false);

  async function getHom() {
    const actualXX = mXX;
    const actualYY = mYY;
    if (valueX !== "") {
      setmXX(mXX.concat(valueX));
      actualXX.push(valueX);
      setValueX("");
    }
    if (valueY !== "") {
      setmYY(mYY.concat(valueY));
      actualYY.push(valueY);
      setValueY("");
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

    const response = await fetchWithUiFeedback<number>({
      url: "/api/calculator/3/ext",
      method: "POST",
      body: { first: actualXX, second: actualYY, third: n },
    });
    if (response.data === undefined) return;
    setComputeClicked(true);
    setResult(response.data);
    setSelected(actualXX);
    setSecondarySelected(actualYY);
    setHighlighted([]);
  }

  return (
    <Accordion>
      <AccordionSummary expandIcon={<ExpandMoreIcon />}>
        <Typography fontWeight="medium">Hom and Ext</Typography>
      </AccordionSummary>
      <AccordionDetails>
        <Typography mb={2}>
          Compute dim Ext^n(X, Y) for modules X and Y (Hom if n = 0).
        </Typography>
        <Grid
          container
          spacing={1}
          mb={2}
        >
          <Grid item xs={4}>
            <Autocomplete
              multiple
              value={mXX}
              onChange={(_, newValue: string[]) => {
                setmXX(newValue);
                setSelected(newValue);
                setSecondarySelected(mYY);
                setHighlighted([]);
                setComputeClicked(false);
              }}
              inputValue={valueX}
              onInputChange={(event, newInputValue) => {
                setValueX(newInputValue);
                setComputeClicked(false);
              }}
              options={options}
              sx={{ width: "auto" }}
              renderInput={(params) => <TextField {...params} label="X" />}
            />
          </Grid>
          <Grid item xs={4}>
            <Autocomplete
              multiple
              value={mYY}
              onChange={(_, newValue: string[]) => {
                setmYY(newValue);
                setSelected(mXX);
                setSecondarySelected(newValue);
                setHighlighted([]);
                setComputeClicked(false);
              }}
              inputValue={valueY}
              onInputChange={(event, newInputValue) => {
                setValueY(newInputValue);
                setComputeClicked(false);
              }}
              options={options}
              sx={{ width: "auto" }}
              renderInput={(params) => <TextField {...params} label="Y" />}
            />
          </Grid>
          <Grid item xs={2}>
            <TextField
              value={degree}
              onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                setDegree(event.target.value);
                setComputeClicked(false);
              }}
              label="n"
            />
          </Grid>
          <Grid item xs={2} display="flex" justifyContent="center">
            <ComputeButton onClick={getHom} />
          </Grid>
        </Grid>
        <Typography align="center">
          dim Ext^{degree}({mXX.join(" + ")}, {mYY.join(" + ")}) ={" "}
          {computeClicked && result}
        </Typography>
      </AccordionDetails>
    </Accordion>
  );
}
