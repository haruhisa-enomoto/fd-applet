import { useState } from "react";

import { GraphEvents } from "react-vis-graph-wrapper";

import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Box,
  Typography,
} from "@mui/material";

import { Combo } from "../../common/Combo";
import GenericQuiver from "../../common/GenericQuiver";
import { subcatOptions } from "../enumerator/SubcatEnumerator";

import { HassePhysicsOption } from "./QuiversTab";

interface SubcatHasseAccordionProps {
  events: GraphEvents;
}

export default function SubcatHasseAccordion({
  events,
}: SubcatHasseAccordionProps) {
  const [selectedMenu, setSelectedMenu] = useState("tors");

  return (
    <Accordion TransitionProps={{ unmountOnExit: true }}>
      <AccordionSummary expandIcon={<ExpandMoreIcon />}>
        <Typography fontWeight="medium">Hasse quiver of subcategories</Typography>
      </AccordionSummary>
      <AccordionDetails>
        <Box mb={2}>
          <Combo
            title="Choose types"
            options={subcatOptions}
            selected={selectedMenu}
            setSelected={setSelectedMenu}
          />
        </Box>
        <GenericQuiver
          showDuration={true}
          url={"/api/quiver/subcat/" + selectedMenu}
          physicOption={HassePhysicsOption}
          updateButton
          allowChosen
          events={events}
        />
      </AccordionDetails>
    </Accordion>
  );
}
