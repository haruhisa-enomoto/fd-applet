import { useState } from "react";

import { GraphEvents } from "react-vis-graph-wrapper";

import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Divider,
  Grid,
  Typography,
} from "@mui/material";

import { Combo } from "../../common/Combo";
import GenericQuiver from "../../common/GenericQuiver";
import { pairOptions } from "../enumerator/PairEnumerator";

import { HassePhysicsOption } from "./QuiversTab";

interface PairHasseAccordionProps {
  events: GraphEvents;
}

export default function PairHasseAccordion({
  events,
}: PairHasseAccordionProps) {
  const [selectedMenu, setSelectedMenu] = useState("tors");

  return (
    <Accordion TransitionProps={{ unmountOnExit: true }}>
      <AccordionSummary expandIcon={<ExpandMoreIcon />} sx={{ m: 0 }}>
        <Typography>Hasse quiver of pair of subcategories</Typography>
      </AccordionSummary>
      <Divider />
      <AccordionDetails>
        <Grid
          container
          spacing={2}
          justifyContent="space-around"
          alignItems="center"
        >
          <Grid item xs={12}>
            <Typography>
              Hasse quiver of inclusion with respect to 1st subcategories.
            </Typography>
          </Grid>
          <Grid item xs={12}>
            <Combo
              title="Choose types"
              options={pairOptions}
              selected={selectedMenu}
              setSelected={setSelectedMenu}
            />
          </Grid>
          <Grid item xs={12}>
            <GenericQuiver
              url={"/api/quiver/pair/" + selectedMenu}
              physicOption={HassePhysicsOption}
              updateButton
              allowChosen
              events={events}
            />
          </Grid>
        </Grid>
      </AccordionDetails>
    </Accordion>
  );
}
