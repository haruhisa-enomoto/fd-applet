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
import useFetchWithUiFeedback from "../../../hooks/useFetchWithUiFeedback";
import ComputeButton from "../../common/ComputeButton";
import ListListList from "../../common/ListListList";



export default function IceSeqEnumerator() {
  const fetchWithUiFeedback = useFetchWithUiFeedback();
  const { setSelected, setSecondarySelected, setHighlighted } = useSelection();

  // const [full, setFull] = useState(true);
  // const [proper, setProper] = useState(false);
  const [data, setData] = useState<string[][][]>([[[]]]);
  const [length, setLength] = useState("");
  // const [lengthOption, setLengthOption] = useState("eq");

  async function getData() {
    const params = new URLSearchParams();
    params.append("full", "true");
    // if (proper) params.append("proper", "true");
    params.append("bound", "false");
    params.append("length", length);
    const response = await fetchWithUiFeedback<string[][][]>({
      url: `/api/others/ice_seq?${params.toString()}`,
      showDuration: true
    });
    if (response.data === undefined) return;
    console.log(response.data);
    setData(response.data);
    setSelected([]);
    setSecondarySelected([]);
    setHighlighted([]);
  }

  return (
    <Accordion>
      <AccordionSummary expandIcon={<ExpandMoreIcon />}>
        <Typography fontWeight="medium">ICE sequences</Typography>
      </AccordionSummary>
      <AccordionDetails>
        <Typography mb={2}>
          Decreasing sequences of ICE-closed subcategories satisfying some conditions. This corresponds to <b>(length)-intermediate t-structures</b> of the bounded derived category whose aisles are homology determined. See [A. Sakai, arXiv:2307.11347] for more details.
          <br />
          Note the blank corresponds to the zero subcategory.
        </Typography>
        <Grid
          container
          spacing={2}
          mb={2}
        >
          {/* <Grid item xs={6}>
            <FormControlLabel
              label="Full (mod A ⊇ … ⊇ 0)"
              control={<Checkbox
                checked={full}
                onChange={(event) => setFull(event.target.checked)}
              />}
            />
          </Grid>
          <Grid item xs={6}>
            <FormControlLabel
              label="Proper (… ⊋ …)"
              control={<Checkbox
                checked={proper}
                onChange={(event) => setProper(event.target.checked)}
              />}
            />
          </Grid> */}
          {/* <Grid item xs={5}>
            <RadioGroup row value={lengthOption} onChange={(event) => setLengthOption(event.target.value)}>
              <FormControlLabel value="eq" control={<Radio />} label="length =" />
              <FormControlLabel value="leq" control={<Radio />} label="length <=" />
            </RadioGroup>
          </Grid> */}
          <Grid item xs={10}>
            <TextField
              value={length}
              onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                setLength(event.target.value);
              }}
              label="Length"
              fullWidth
            />
          </Grid>
          <Grid item xs={2} display="flex" justifyContent="center">
            <ComputeButton onClick={getData} />
          </Grid>
        </Grid>
        <ListListList
          candidates={data}
          handleMiddleChange={(value) => {
            setSelected(value);
            setSecondarySelected([]);
            setHighlighted([]);
          }}
        />
      </AccordionDetails>
    </Accordion>
  );
}
